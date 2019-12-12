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

package co.anitrend.support.crunchyroll.data.arch.controller

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingRequestHelper
import co.anitrend.arch.data.common.ISupportPagingResponse
import co.anitrend.arch.data.common.ISupportResponse
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.capitalizeWords
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

internal class CrunchyController<S, D> private constructor(
    private val responseMapper: CrunchyMapper<S, D>,
    private val supportConnectivity: SupportConnectivity,
    private val dispatchers: SupportDispatchers
) : ISupportResponse<Deferred<Response<CrunchyContainer<S>>>, D>,
    ISupportPagingResponse<Deferred<Response<CrunchyContainer<S>>>> {

    private val moduleTag: String = javaClass.simpleName

    private suspend fun connectedRun(
        block: suspend () -> D?,
        networkState: MutableLiveData<NetworkState>
    ): D? {
        if (supportConnectivity.isConnected)
            return block()

        networkState.postValue(
            NetworkState.Error(
                heading = "No Internet Connection",
                message = "Please check your internet connection"
            )
        )
        return null
    }

    private suspend fun connectedRun(
        block: suspend () -> Unit,
        pagingRequestHelper: PagingRequestHelper.Request.Callback
    ) {
        if (supportConnectivity.isConnected)
            block()
        else
            pagingRequestHelper.recordFailure(
                Throwable("Please check your internet connection")
            )
    }

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
        return connectedRun({
            networkState.postValue(NetworkState.Loading)
            val result = runCatching {
                val response = withContext(dispatchers.io) {
                    resource.await()
                }
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.error == false) {
                        val result = if (responseBody.data != null) {
                            val mapped = responseMapper.onResponseMapFrom(responseBody.data)
                            withContext(dispatchers.io) {
                                responseMapper.onResponseDatabaseInsert(mapped)
                            }
                            mapped
                        } else null
                        networkState.postValue(NetworkState.Success)
                        result
                    } else {
                        Timber.tag(moduleTag).e("${responseBody?.message} | Status: ${responseBody?.code}")
                        networkState.postValue(
                            NetworkState.Error(
                                heading = responseBody?.code?.name.capitalizeWords(),
                                message = responseBody?.message
                            )
                        )
                        null
                    }
                } else {
                    networkState.postValue(
                        NetworkState.Error(
                            heading = "Server Request/Response Error",
                            message = response.message()
                        )
                    )
                    null
                }
            }

            result.getOrElse {
                it.printStackTrace()
                networkState.postValue(
                    NetworkState.Error(
                        heading = "Internal Application Error",
                        message = it.message
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
        connectedRun({
            val result = runCatching {
                val response = withContext(dispatchers.io) {
                    resource.await()
                }
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            responseBody.data?.apply {
                                val mapped = responseMapper.onResponseMapFrom(this)
                                withContext(dispatchers.io) {
                                    responseMapper.onResponseDatabaseInsert(mapped)
                                }
                            }
                        } else {
                            Timber.tag(moduleTag).e("${responseBody.message} | Status: ${responseBody.code}")
                            pagingRequestHelper.recordFailure(
                                Throwable(responseBody.message ?: "Unknown error occurred")
                            )
                        }
                    }
                    pagingRequestHelper.recordSuccess()
                } else {
                    pagingRequestHelper.recordFailure(
                        Throwable(response.message())
                    )
                }
            }

            result.getOrElse {
                it.printStackTrace()
                Timber.tag(moduleTag).e(it)
                pagingRequestHelper.recordFailure(it)
            }
        }, pagingRequestHelper)
    }


    companion object {
        fun <S, D> newInstance(
            responseMapper: CrunchyMapper<S, D>,
            supportConnectivity: SupportConnectivity,
            supportDispatchers: SupportDispatchers
        ) = CrunchyController(
            responseMapper,
            supportConnectivity,
            supportDispatchers
        )
    }
}