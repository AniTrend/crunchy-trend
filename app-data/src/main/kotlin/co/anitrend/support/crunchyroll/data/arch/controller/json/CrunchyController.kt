/*
 *    Copyright 2020 AniTrend
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package co.anitrend.support.crunchyroll.data.arch.controller.json

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingRequestHelper
import co.anitrend.arch.data.common.ISupportPagingResponse
import co.anitrend.arch.data.common.ISupportResponse
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.capitalizeWords
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.contract.ControllerStrategy
import co.anitrend.support.crunchyroll.data.arch.extension.fetchBodyWithRetry
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

internal class CrunchyController<S, D> private constructor(
    private val responseMapper: CrunchyMapper<S, D>,
    private val strategy: ControllerStrategy<D>,
    private val dispatchers: SupportDispatchers
) : ISupportResponse<Deferred<Response<CrunchyContainer<S>>>, D>,
    ISupportPagingResponse<Deferred<Response<CrunchyContainer<S>>>> {

    private val moduleTag: String = javaClass.simpleName

    /**
     * Response handler for coroutine contexts which need to observe [NetworkState]
     *
     * @param resource awaiting execution
     * @param networkState for the deferred result
     *
     * @return resource fetched if present
     */
    override suspend fun invoke(
        resource: Deferred<Response<CrunchyContainer<S>>>,
        networkState: MutableLiveData<NetworkState>
    ): D? {
        return strategy({
            networkState.postValue(NetworkState.Loading)
            val response = resource.fetchBodyWithRetry(dispatchers.io)
            if (!response.error) {
                val result = if (response.data != null) {
                    val mapped = responseMapper.onResponseMapFrom(response.data)
                    withContext(dispatchers.io) {
                        responseMapper.onResponseDatabaseInsert(mapped)
                    }
                    mapped
                } else null
                networkState.postValue(NetworkState.Success)
                result
            } else {
                Timber.tag(moduleTag).e(
                    "${response.message} | Status: ${response.code}"
                )
                networkState.postValue(
                    NetworkState.Error(
                        heading = response.code.name.capitalizeWords(),
                        message = response.message
                    )
                )
                null
            }
        }, networkState)
    }

    /**
     * Response handler for coroutine contexts, mainly for paging
     *
     * @param resource awaiting execution
     * @param pagingRequestHelper optional paging request callback
     */
    override suspend fun invoke(
        resource: Deferred<Response<CrunchyContainer<S>>>,
        pagingRequestHelper: PagingRequestHelper.Request.Callback
    ) {
        strategy({
            val response = resource.fetchBodyWithRetry(dispatchers.io)
            if (!response.error) {
                response.data?.apply {
                    val mapped = responseMapper.onResponseMapFrom(this)
                    withContext(dispatchers.io) {
                        responseMapper.onResponseDatabaseInsert(mapped)
                    }
                }
                pagingRequestHelper.recordSuccess()
            } else {
                Timber.tag(moduleTag).e("${response.message} | Status: ${response.code}")
                pagingRequestHelper.recordFailure(
                    Throwable(response.message ?: "Unexpected error occurred")
                )
            }
        }, pagingRequestHelper)
    }


    companion object {
        fun <S, D> newInstance(
            strategy: ControllerStrategy<D>,
            responseMapper: CrunchyMapper<S, D>,
            supportDispatchers: SupportDispatchers
        ) =
            CrunchyController(
                strategy = strategy,
                responseMapper = responseMapper,
                dispatchers = supportDispatchers
            )
    }
}