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

package co.anitrend.support.crunchyroll.data.series.source.search.contract

import androidx.paging.PagedList
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.support.crunchyroll.data.arch.common.CrunchyPagedSource
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery
import kotlinx.coroutines.flow.Flow

internal abstract class SeriesSearchSource : CrunchyPagedSource<CrunchySeries>() {

    protected lateinit var query: CrunchySeriesSearchQuery
        private set

    protected abstract fun observable(
        searchQuery: CrunchySeriesSearchQuery
    ): Flow<PagedList<CrunchySeries>>

    internal operator fun invoke(
        searchQuery: CrunchySeriesSearchQuery
    ): Flow<PagedList<CrunchySeries>> {
        runCatching {
            // reset paging if our search query has changed
            if (query.searchTerm != searchQuery.searchTerm)
                supportPagingHelper.onPageRefresh()
        }
        query = searchQuery
        return observable(searchQuery)
    }
}