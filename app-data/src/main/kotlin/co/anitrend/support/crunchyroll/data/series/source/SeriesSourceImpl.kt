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

package co.anitrend.support.crunchyroll.data.series.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.util.SupportDataKeyStore
import co.anitrend.support.crunchyroll.data.series.datasource.local.CrunchySeriesDao
import co.anitrend.support.crunchyroll.data.series.datasource.remote.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.series.entity.CrunchySeriesEntity
import co.anitrend.support.crunchyroll.data.series.mapper.SeriesResponseMapper
import co.anitrend.support.crunchyroll.data.series.source.contract.SeriesSource
import co.anitrend.support.crunchyroll.data.series.transformer.CrunchySeriesTransformer
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesQuery
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SeriesSourceImpl(
    private val mapper: SeriesResponseMapper,
    private val seriesDao: CrunchySeriesDao,
    private val seriesEndpoint: CrunchySeriesEndpoint
) : SeriesSource() {

    private fun searchForSeries(
        callback: PagingRequestHelper.Request.Callback,
        param: CrunchySeriesSearchQuery
    ) {
        val deferred = async {
            seriesEndpoint.getSeriesAutoComplete(
                offset = supportPagingHelper.pageOffset,
                limit = supportPagingHelper.pageSize,
                query = param.query
            )
        }

        launch {
            mapper(deferred, callback)
        }
    }

    override val seriesSearchObservable =
        object : ISourceObservable<CrunchySeriesSearchQuery, PagedList<CrunchySeries>> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: CrunchySeriesSearchQuery): LiveData<PagedList<CrunchySeries>> {
                executionTarget = { searchForSeries(it, parameter) }

                val localSource = seriesDao.findBySeriesNameFactory(parameter.query)

                val result = localSource.map {
                    CrunchySeriesTransformer.transform(it)
                }

                return result.toLiveData(
                    config = SupportDataKeyStore.PAGING_CONFIGURATION,
                    boundaryCallback = this@SeriesSourceImpl
                )
            }
        }

    override val seriesObservable =
        object : ISourceObservable<CrunchySeriesQuery, CrunchySeries?> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: CrunchySeriesQuery): LiveData<CrunchySeries?> {
                val localSource = seriesDao.findBySeriesIdX(parameter.seriesId)

                return Transformations.map(localSource) {
                    it?.let { s-> CrunchySeriesTransformer.transform(s) }
                }
            }
        }


    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        seriesDao.clearTable()
    }
}