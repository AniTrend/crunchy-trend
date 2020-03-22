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
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.CrunchyRssMediaController
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyRssMapper
import co.anitrend.support.crunchyroll.data.rss.contract.IRssCopyright
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.episode.entities.CrunchyEpisodeFeed
import kotlinx.coroutines.launch

abstract class EpisodeFeedSource(
    supportDispatchers: SupportDispatchers
) : SupportPagingDataSource<CrunchyEpisodeFeed>(supportDispatchers) {

    abstract val episodeListingsObservable: ISourceObservable<RssQuery, PagedList<CrunchyEpisodeFeed>>

    protected abstract fun getMediaListingsCatalogue(
        callback: PagingRequestHelper.Request.Callback
    )

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        pagingRequestHelper.runIfNotRunning(
            PagingRequestHelper.RequestType.INITIAL
        ) {
            if (supportPagingHelper.isFirstPage()) {
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
            if (supportPagingHelper.isFirstPage()) {
                getMediaListingsCatalogue(it)
                supportPagingHelper.onPageNext()
            }
            else it.recordSuccess()
        }
    }

    /**
     * Extension to help us create a controller from a a mapper instance
     */
    internal fun <S: IRssCopyright, D> CrunchyRssMapper<S, D>.controller(
        supportConnectivity: SupportConnectivity
    ) = CrunchyRssMediaController.newInstance(
        responseMapper = this,
        supportConnectivity = supportConnectivity,
        supportDispatchers = dispatchers
    )
}