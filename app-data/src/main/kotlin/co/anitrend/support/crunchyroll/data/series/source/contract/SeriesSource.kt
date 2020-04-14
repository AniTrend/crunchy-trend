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
import co.anitrend.support.crunchyroll.data.arch.CrunchyExperimentalFeature
import co.anitrend.support.crunchyroll.data.arch.common.CrunchyPagedSource
import co.anitrend.support.crunchyroll.data.arch.database.dao.ISourceDao
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesBrowseQuery
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesInfoQuery
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery

abstract class SeriesSource(
    supportDispatchers: SupportDispatchers,
    sourceDao: ISourceDao
) : CrunchyPagedSource<CrunchySeries>(supportDispatchers, sourceDao) {

    abstract val seriesSearchObservable:
            ISourceObservable<CrunchySeriesSearchQuery, PagedList<CrunchySeries>>

    abstract val seriesBrowseObservable:
            ISourceObservable<CrunchySeriesBrowseQuery, PagedList<CrunchySeries>>

    abstract val seriesInfoObservable:
            ISourceObservable<CrunchySeriesInfoQuery, CrunchySeries?>

    /**
     * Since we plan on using a paging source backed by a database, Ideally we should
     * configure [supportPagingHelper] with the records count/paging limit to start load
     * from the last page of results in our backend.
     *
     * @param sourceDao contract for all compatible data access objects
     *
     * @see setUpPagingHelperWithInitial
     */
    @CrunchyExperimentalFeature
    override suspend fun configurePagingHelper(sourceDao: ISourceDao) {
        val count = sourceDao.count()
        setUpPagingHelperWithInitial(count)
    }
}