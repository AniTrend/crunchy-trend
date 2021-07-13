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

package co.anitrend.support.crunchyroll.data.news.source.contract

import androidx.paging.PagedList
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.model.Request
import co.anitrend.arch.data.source.paging.SupportPagingDataSource
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal abstract class NewsSource : SupportPagingDataSource<CrunchyNews>() {

    protected lateinit var query: RssQuery

    protected abstract fun observable(query: RssQuery): Flow<PagedList<CrunchyNews>>

    protected abstract suspend fun getNewsCatalogue(
        callback: RequestCallback
    )

    fun newsCatalogue(query: RssQuery): Flow<PagedList<CrunchyNews>> {
        this.query = query
        return observable(query)
    }

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        if (supportPagingHelper.isFirstPage()) {
            launch {
                requestHelper.runIfNotRunning(
                    Request.Default(
                        "news_source_initial",
                        Request.Type.INITIAL
                    )
                ) { getNewsCatalogue(it) }
            }
            supportPagingHelper.onPageNext()
        }
    }

    /**
     * Called when the item at the front of the PagedList has been loaded, and access has
     * occurred within [Config.prefetchDistance] of it.
     *
     *
     * No more data will be prepended to the PagedList before this item.
     *
     * @param itemAtFront The first item of PagedList
     */
    override fun onItemAtFrontLoaded(itemAtFront: CrunchyNews) {
        if (supportPagingHelper.isFirstPage()) {
            launch {
                requestHelper.runIfNotRunning(
                    Request.Default(
                        "news_source_before",
                        Request.Type.BEFORE
                    )
                ) { getNewsCatalogue(it) }
            }
            supportPagingHelper.onPageNext()
        }
    }
}