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

package co.anitrend.support.crunchyroll.data.series.source.browse

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.contract.IRequestHelper
import co.anitrend.arch.data.util.PAGING_CONFIGURATION
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.ext.empty
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyPagingConfigHelper
import co.anitrend.support.crunchyroll.data.series.converters.SeriesEntityConverter
import co.anitrend.support.crunchyroll.data.series.datasource.local.CrunchySeriesDao
import co.anitrend.support.crunchyroll.data.series.datasource.remote.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.series.mapper.SeriesResponseMapper
import co.anitrend.support.crunchyroll.data.series.source.browse.contract.SeriesBrowseSource
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchySeriesBrowseFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

internal class SeriesBrowseSourceImpl(
    private val mapper: SeriesResponseMapper,
    private val seriesDao: CrunchySeriesDao,
    private val endpoint: CrunchySeriesEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    supportDispatchers: SupportDispatchers
) : SeriesBrowseSource(supportDispatchers) {

    override val observable = liveData {
        val localSource = when (query.filter) {
            CrunchySeriesBrowseFilter.ALPHA ->
                seriesDao.findAllFactory()
            CrunchySeriesBrowseFilter.PREFIX -> {
                val prefix = buildQueryForDatabase()
                seriesDao.findAllStartingWithFactory(prefix)
            }
            CrunchySeriesBrowseFilter.TAG -> {
                val genre = buildQueryForDatabase()
                seriesDao.findAllContainingGenreFactory(genre)
            }
        }

        val result = localSource.map {
            SeriesEntityConverter.convertFrom(it)
        }

        emitSource(
            result.toLiveData(
                config = PAGING_CONFIGURATION,
                boundaryCallback = this@SeriesBrowseSourceImpl
            )
        )
    }

    @SuppressLint("DefaultLocale")
    private fun buildQueryForDatabase(): String {
        if (query.filter == CrunchySeriesBrowseFilter.PREFIX)
            return "${query.option}%"
        else if (query.filter == CrunchySeriesBrowseFilter.TAG)
            return "%${query.option.toLowerCase()}%"
        return String.empty()
    }

    private suspend fun buildFilterForRequest(
        requestType: IRequestHelper.RequestType
    ): String {
        return when (query.filter) {
            CrunchySeriesBrowseFilter.ALPHA -> {
                CrunchyPagingConfigHelper(requestType, supportPagingHelper) {
                    seriesDao.count()
                }
                query.filter.attribute
            }
            CrunchySeriesBrowseFilter.PREFIX -> {
                val prefix = "${query.filter.attribute}${query.option.toLowerCase()}"
                CrunchyPagingConfigHelper(requestType, supportPagingHelper) {
                    seriesDao.countStartingWith(prefix)
                }
                prefix
            }
            CrunchySeriesBrowseFilter.TAG ->{
                val genre = "${query.filter.attribute}${query.option.toLowerCase()}"
                CrunchyPagingConfigHelper(requestType, supportPagingHelper) {
                    seriesDao.countContainingGenre(genre)
                }
                genre
            }
        }
    }

    override suspend fun invoke(
        callback: RequestCallback,
        requestType: IRequestHelper.RequestType,
        model: CrunchySeries?
    ) {
        val filter = buildFilterForRequest(requestType)

        val deferred = async {
            endpoint.getSeriesList(
                offset = supportPagingHelper.pageOffset,
                limit = supportPagingHelper.pageSize,
                filter = filter
            )
        }

        val controller =
            mapper.controller(
                dispatchers,
                OnlineControllerPolicy.create(
                    supportConnectivity
                )
            )

        controller(deferred, callback)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            withContext(context) {
                when (query.filter) {
                    CrunchySeriesBrowseFilter.ALPHA ->
                        seriesDao.clearTable()
                    CrunchySeriesBrowseFilter.PREFIX -> {
                        val prefix = buildQueryForDatabase()
                        seriesDao.clearTableByPrefix(prefix)
                    }
                    CrunchySeriesBrowseFilter.TAG -> {
                        val genre = buildQueryForDatabase()
                        seriesDao.clearTableByGenre(genre)
                    }
                }
            }
        }
    }
}