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

import androidx.paging.PagingRequestHelper
import co.anitrend.arch.data.common.ISupportPagingResponse
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.capitalizeWords
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyRssMapper
import co.anitrend.support.crunchyroll.data.rss.contract.ICrunchyRssChannel
import co.anitrend.support.crunchyroll.data.rss.contract.IRssCopyright
import co.anitrend.support.crunchyroll.data.rss.core.CrunchyRssNewsContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

internal class CrunchyRssNewsController<S: IRssCopyright, D> private constructor(
    private val responseMapper: CrunchyRssMapper<S, D>,
    private val supportConnectivity: SupportConnectivity,
    private val dispatchers: SupportDispatchers
) : ISupportPagingResponse<Deferred<Response<CrunchyRssNewsContainer>>> {

    private val moduleTag: String = javaClass.simpleName

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
     * Response handler for coroutine contexts, mainly for paging
     *
     * @param resource awaiting execution
     * @param pagingRequestHelper optional paging request callback
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun invoke(
        resource: Deferred<Response<CrunchyRssNewsContainer>>,
        pagingRequestHelper: PagingRequestHelper.Request.Callback
    ) {
        connectedRun({
            val result = runCatching {
                val response = withContext(dispatchers.io) {
                    resource.await()
                }
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.channel != null) {
                        val mapped = responseMapper.onResponseMapFrom(
                            responseBody.channel as ICrunchyRssChannel<S>
                        )
                        withContext(dispatchers.io) {
                            responseMapper.onResponseDatabaseInsert(mapped)
                        }
                        pagingRequestHelper.recordSuccess()
                    } else {
                        pagingRequestHelper.recordFailure(
                            Throwable(response.message().capitalizeWords())
                        )
                    }
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
        fun <S: IRssCopyright, D> newInstance(
            responseMapper: CrunchyRssMapper<S, D>,
            supportConnectivity: SupportConnectivity,
            supportDispatchers: SupportDispatchers
        ) = CrunchyRssNewsController(
            responseMapper,
            supportConnectivity,
            supportDispatchers
        )
    }
}