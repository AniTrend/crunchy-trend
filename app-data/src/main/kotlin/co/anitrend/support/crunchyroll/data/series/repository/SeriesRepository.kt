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

package co.anitrend.support.crunchyroll.data.series.repository

import androidx.paging.PagedList
import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.arch.data.repository.SupportRepository
import co.anitrend.support.crunchyroll.data.series.source.contract.SeriesSource
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesQuery
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery
import co.anitrend.support.crunchyroll.domain.series.repositories.ISeriesInfoRepository
import co.anitrend.support.crunchyroll.domain.series.repositories.ISeriesSearchRepository

class SeriesRepository(
    private val source: SeriesSource
) : SupportRepository(source),
    ISeriesSearchRepository<UserInterfaceState<PagedList<CrunchySeries>>>,
    ISeriesInfoRepository<UserInterfaceState<CrunchySeries?>> {

    override fun getSeries(
        seriesQuery: CrunchySeriesQuery
    ) =
        UserInterfaceState.create(
            model = source.seriesObservable(seriesQuery),
            source = source
        )

    override fun searchForSeries(
        searchQuery: CrunchySeriesSearchQuery
    ) =
        UserInterfaceState.create(
            model = source.seriesSearchObservable(searchQuery),
            source = source
    )
}