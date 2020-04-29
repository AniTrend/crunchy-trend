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

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.source.paging.SupportPagingDataSource
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews
import kotlinx.coroutines.launch

internal abstract class NewsSource(
    supportDispatchers: SupportDispatchers
) : SupportPagingDataSource<CrunchyNews>(supportDispatchers) {

    protected lateinit var query: RssQuery
        private set

    protected abstract val newsObservable: ISourceObservable<Nothing?, PagedList<CrunchyNews>>

    protected abstract suspend fun getNewsCatalogue(
        callback: PagingRequestHelper.Request.Callback
    )

    operator fun invoke(rssQuery: RssQuery): LiveData<PagedList<CrunchyNews>> {
        query = rssQuery
        return newsObservable(null)
    }

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        pagingRequestHelper.runIfNotRunning(
            PagingRequestHelper.RequestType.INITIAL
        ) {
            if (supportPagingHelper.isFirstPage()) {
                launch{ getNewsCatalogue(it) }
                supportPagingHelper.onPageNext()
            }
            else it.recordSuccess()
        }
    }

    /**
     * Called when the item at the front of the PagedList has been loaded, and access has
     * occurred within [Config.prefetchDistance] of it.
     *
     * No more data will be prepended to the PagedList before this item.
     *
     * @param itemAtFront The first item of PagedList
     */
    override fun onItemAtFrontLoaded(itemAtFront: CrunchyNews) {
        pagingRequestHelper.runIfNotRunning(
            PagingRequestHelper.RequestType.BEFORE
        ) {
            if (supportPagingHelper.isFirstPage()) {
                launch { getNewsCatalogue(it) }
                supportPagingHelper.onPageNext()
            }
            else it.recordSuccess()
        }
    }
}