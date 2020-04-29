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

package co.anitrend.support.crunchyroll.data.series.repository.detail

import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.arch.data.model.UserInterfaceState.Companion.create
import co.anitrend.arch.data.repository.SupportRepository
import co.anitrend.support.crunchyroll.data.series.source.detail.contract.SeriesDetailSource
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesDetailQuery
import co.anitrend.support.crunchyroll.domain.series.repositories.ISeriesDetailRepository

internal class SeriesDetailRepository(
    private val source: SeriesDetailSource
) : SupportRepository(source), ISeriesDetailRepository<UserInterfaceState<CrunchySeries?>> {

    override fun getSeries(
        seriesDetailQuery: CrunchySeriesDetailQuery
    ) =
        source.create(
            model = source(seriesDetailQuery)
        )
}