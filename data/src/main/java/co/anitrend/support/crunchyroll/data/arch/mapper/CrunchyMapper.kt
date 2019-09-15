/*
 *    Copyright 2019 AniTrend
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

package co.anitrend.support.crunchyroll.data.arch.mapper

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingRequestHelper
import co.anitrend.arch.data.common.ISupportPagingResponse
import co.anitrend.arch.data.common.ISupportResponse
import co.anitrend.arch.data.mapper.SupportResponseMapper
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.capitalizeWords
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import kotlinx.coroutines.Deferred
import retrofit2.Response
import timber.log.Timber

abstract class CrunchyMapper<S, D> : SupportResponseMapper<S, D>(),
    ISupportPagingResponse<Deferred<Response<CrunchyContainer<S>>>>,
    ISupportResponse<Deferred<Response<CrunchyContainer<S>>>, D> {

    /**
     * Response handler for coroutine contexts which need to observe
     * the live data of [NetworkState]
     *
     * Unless when if using [androidx.paging.PagingRequestHelper.Request.Callback]
     * then you can ignore the return type
     *
     * @param resource an deferred result awaiting execution
     * @return network state of the deferred result
     */
    override suspend fun invoke(
        resource: Deferred<Response<CrunchyContainer<S>>>,
        pagingRequestHelper: PagingRequestHelper.Request.Callback
    ) {
        val result = runCatching {
            val response = resource.await()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    if (!responseBody.error) {
                        if (responseBody.data != null) {
                            val mapped = onResponseMapFrom(responseBody.data)
                            onResponseDatabaseInsert(mapped)
                        }
                    } else
                        pagingRequestHelper.recordFailure(Throwable(responseBody.message))
                }
                pagingRequestHelper.recordSuccess()
            } else
                pagingRequestHelper.recordFailure(Throwable(response.message()))
        }

        return result.getOrElse {
            it.printStackTrace()
            Timber.tag(moduleTag).e(it)
            NetworkState.Error(
                heading = "Internal Application Error",
                message = it.message
            )
        }
    }

    /**
     * Response handler for coroutine contexts which need to observe [NetworkState]
     *
     * @param resource awaiting execution
     * @param networkState for the deferred result
     */
    override suspend fun invoke(
        resource: Deferred<Response<CrunchyContainer<S>>>,
        networkState: MutableLiveData<NetworkState>
    ) : D? {
        val result = runCatching {
            val response = resource.await()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody?.error == false) {
                    val result = if (responseBody.data != null) {
                        val mapped = onResponseMapFrom(responseBody.data)
                        onResponseDatabaseInsert(mapped)
                        mapped
                    } else null
                    networkState.postValue(NetworkState.Success)
                    result
                } else {
                    networkState.postValue(
                        NetworkState.Error(
                            heading = responseBody?.code.capitalizeWords(),
                            message = responseBody?.message
                        )
                    )
                    null
                }
            } else {
                networkState.postValue(
                    NetworkState.Error(
                        heading = "Network Error",
                        message = response.message()
                    )
                )
                null
            }
        }

        return result.getOrElse {
            it.printStackTrace()
            NetworkState.Error(
                heading = "Internal Application Error",
                message = it.message
            )
            null
        }
    }
}