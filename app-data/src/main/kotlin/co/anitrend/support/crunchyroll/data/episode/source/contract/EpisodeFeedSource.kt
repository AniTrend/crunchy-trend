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

package co.anitrend.support.crunchyroll.data.episode.source.contract

import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.source.paging.SupportPagingDataSource
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.episode.entities.CrunchyEpisodeFeed
import kotlinx.coroutines.launch

abstract class EpisodeFeedSource : SupportPagingDataSource<CrunchyEpisodeFeed>() {

    abstract val episodeListingsObservable: ISourceObservable<RssQuery, PagedList<CrunchyEpisodeFeed>>

    protected abstract suspend fun getMediaListingsCatalogue(
        callback: PagingRequestHelper.Request.Callback
    )

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        pagingRequestHelper.runIfNotRunning(
            PagingRequestHelper.RequestType.INITIAL
        ) {
            if (supportPagingHelper.isFirstPage())
                launch {
                    getMediaListingsCatalogue(it)
                    supportPagingHelper.onPageNext()
                }
            else it.recordSuccess()
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
    override fun onItemAtFrontLoaded(itemAtFront: CrunchyEpisodeFeed) {
        pagingRequestHelper.runIfNotRunning(
            PagingRequestHelper.RequestType.BEFORE
        ) {
            if (connectivityHelper.isConnected) {
                if (supportPagingHelper.isFirstPage())
                    launch {
                        getMediaListingsCatalogue(it)
                        supportPagingHelper.onPageNext()
                    }
                else it.recordSuccess()
            } else it.recordSuccess()
        }
    }
}