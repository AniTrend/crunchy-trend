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

package co.anitrend.support.crunchyroll.data.series.source.browse.contract

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.support.crunchyroll.data.arch.common.CrunchyPagedSource
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesBrowseQuery

internal abstract class SeriesBrowseSource(
    supportDispatchers: SupportDispatchers
) : CrunchyPagedSource<CrunchySeries>(supportDispatchers) {

    protected lateinit var query: CrunchySeriesBrowseQuery
        private set

    protected abstract val observable: LiveData<PagedList<CrunchySeries>>

    internal operator fun invoke(browseQuery: CrunchySeriesBrowseQuery): LiveData<PagedList<CrunchySeries>> {
        runCatching {
            // We're going to reset paging if we happen to change the query with the same instance
            // not likely to happen but better safe than sorry...
            if (query.filter != browseQuery.filter)
                supportPagingHelper.onPageRefresh()
        }
        query = browseQuery
        return observable
    }
}