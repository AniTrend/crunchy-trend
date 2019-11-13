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

import androidx.paging.PagingRequestHelper
import co.anitrend.arch.data.common.ISupportPagingResponse
import co.anitrend.arch.data.mapper.SupportResponseMapper
import co.anitrend.arch.extension.capitalizeWords
import co.anitrend.support.crunchyroll.data.rss.contract.ICrunchyRssChannel
import co.anitrend.support.crunchyroll.data.rss.contract.IRssCopyright
import co.anitrend.support.crunchyroll.data.rss.core.CrunchyRssEpisodeContainer
import co.anitrend.support.crunchyroll.data.rss.core.CrunchyRssNewsContainer
import kotlinx.coroutines.Deferred
import retrofit2.Response
import timber.log.Timber

@Suppress("UNCHECKED_CAST")
abstract class CrunchyRssMapper<D : IRssCopyright> :
    SupportResponseMapper<ICrunchyRssChannel<D>, List<D>>() {

    val news =
        object : ISupportPagingResponse<Deferred<Response<CrunchyRssNewsContainer>>> {

            /**
             * Response handler for coroutine contexts, mainly for paging
             *
             * @param resource awaiting execution
             * @param pagingRequestHelper optional paging request callback
             */
            override suspend operator fun invoke(
                resource: Deferred<Response<CrunchyRssNewsContainer>>,
                pagingRequestHelper: PagingRequestHelper.Request.Callback
            ) {
                val result = runCatching {
                    val response = resource.await()
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody?.channel != null) {
                        val mapped = onResponseMapFrom(
                            responseBody.channel as ICrunchyRssChannel<D>
                        )
                        onResponseDatabaseInsert(mapped)
                        pagingRequestHelper.recordSuccess()
                    } else {
                        pagingRequestHelper.recordFailure(
                            Throwable(response.message().capitalizeWords())
                        )
                    }
                }

                result.getOrElse {
                    it.printStackTrace()
                    Timber.tag(moduleTag).e(it)
                    pagingRequestHelper.recordFailure(it)
                }
            }
        }

    val media =
        object : ISupportPagingResponse<Deferred<Response<CrunchyRssEpisodeContainer>>> {

            /**
             * Response handler for coroutine contexts, mainly for paging
             *
             * @param resource awaiting execution
             * @param pagingRequestHelper optional paging request callback
             */
            override suspend operator fun invoke(
                resource: Deferred<Response<CrunchyRssEpisodeContainer>>,
                pagingRequestHelper: PagingRequestHelper.Request.Callback
            ) {
                val result = runCatching {
                    val response = resource.await()
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody?.channel != null) {
                        val mapped = onResponseMapFrom(
                            responseBody.channel as ICrunchyRssChannel<D>
                        )
                        onResponseDatabaseInsert(mapped)
                        pagingRequestHelper.recordSuccess()
                    } else {
                        pagingRequestHelper.recordFailure(
                            Throwable(response.errorBody()?.string())
                        )
                    }
                }

                result.getOrElse {
                    it.printStackTrace()
                    Timber.tag(moduleTag).e(it)
                    pagingRequestHelper.recordFailure(it)
                }
            }
        }
}