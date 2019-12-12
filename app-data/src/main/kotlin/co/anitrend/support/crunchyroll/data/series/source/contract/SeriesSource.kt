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

package co.anitrend.support.crunchyroll.data.series.source.contract

import androidx.paging.PagedList
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.data.arch.common.CrunchyPagedSource
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesBrowseQuery
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesInfoQuery
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery

abstract class SeriesSource(
    supportDispatchers: SupportDispatchers
) : CrunchyPagedSource<CrunchySeries>(supportDispatchers) {

    abstract val seriesSearchObservable:
            ISourceObservable<CrunchySeriesSearchQuery, PagedList<CrunchySeries>>

    abstract val seriesBrowseObservable:
            ISourceObservable<CrunchySeriesBrowseQuery, PagedList<CrunchySeries>>

    abstract val seriesInfoObservable:
            ISourceObservable<CrunchySeriesInfoQuery, CrunchySeries?>

}