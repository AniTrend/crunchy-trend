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

package co.anitrend.support.crunchyroll.data.arch.controller.xml

import androidx.paging.PagingRequestHelper
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.contract.ControllerStrategy
import co.anitrend.support.crunchyroll.data.arch.extension.fetchBodyWithRetry
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyRssMapper
import co.anitrend.support.crunchyroll.data.rss.contract.ICrunchyContainer
import co.anitrend.support.crunchyroll.data.rss.contract.ICrunchyRssChannel
import co.anitrend.support.crunchyroll.data.rss.contract.IRssCopyright
import co.anitrend.support.crunchyroll.data.rss.core.CrunchyRssEpisodeContainer
import co.anitrend.support.crunchyroll.data.rss.core.CrunchyRssNewsContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import retrofit2.Response

internal class CrunchyXmlController<S: IRssCopyright, D> private constructor(
    private val responseMapper: CrunchyRssMapper<S, D>,
    private val strategy: ControllerStrategy<D>,
    private val dispatchers: SupportDispatchers
) {

    private suspend fun handleChannel(response: ICrunchyContainer<ICrunchyRssChannel<S>>) {
        if (response.channel != null) {
            val mapped = responseMapper.onResponseMapFrom(
                response.channel as ICrunchyRssChannel<S>
            )
            withContext(dispatchers.io) {
                responseMapper.onResponseDatabaseInsert(mapped)
            }
        } else
            throw Throwable("Unexpected error occurred, channel was null")
    }

    /**
     * Response handler for coroutine contexts, mainly for paging
     *
     * @param resource awaiting execution
     * @param pagingRequestHelper optional paging request callback
     */
    suspend fun news(
        resource: Deferred<Response<CrunchyRssNewsContainer>>,
        pagingRequestHelper: PagingRequestHelper.Request.Callback
    ) {
        strategy({
            val response = resource.fetchBodyWithRetry(dispatchers.io)
            @Suppress("UNCHECKED_CAST")
            handleChannel(response as ICrunchyContainer<ICrunchyRssChannel<S>>)
        }, pagingRequestHelper)
    }

    /**
     * Response handler for coroutine contexts, mainly for paging
     *
     * @param resource awaiting execution
     * @param pagingRequestHelper optional paging request callback
     */
    suspend fun episode(
        resource: Deferred<Response<CrunchyRssEpisodeContainer>>,
        pagingRequestHelper: PagingRequestHelper.Request.Callback
    ) {
        strategy({
            val response = resource.fetchBodyWithRetry(dispatchers.io)
            @Suppress("UNCHECKED_CAST")
            handleChannel(response as ICrunchyContainer<ICrunchyRssChannel<S>>)
        }, pagingRequestHelper)
    }

    companion object {
        fun <S: IRssCopyright, D> newInstance(
            strategy: ControllerStrategy<D>,
            responseMapper: CrunchyRssMapper<S, D>,
            supportDispatchers: SupportDispatchers
        ) =
            CrunchyXmlController(
                strategy = strategy,
                responseMapper = responseMapper,
                dispatchers = supportDispatchers
            )
    }
}