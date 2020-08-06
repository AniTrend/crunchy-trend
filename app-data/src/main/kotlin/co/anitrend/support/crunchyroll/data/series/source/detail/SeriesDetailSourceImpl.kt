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

package co.anitrend.support.crunchyroll.data.series.source.detail

import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.series.converters.SeriesEntityConverter
import co.anitrend.support.crunchyroll.data.series.datasource.local.CrunchySeriesDao
import co.anitrend.support.crunchyroll.data.series.datasource.remote.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.series.helper.SeriesCacheHelper
import co.anitrend.support.crunchyroll.data.series.mapper.SeriesDetailResponseMapper
import co.anitrend.support.crunchyroll.data.series.source.detail.contract.SeriesDetailSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

internal class SeriesDetailSourceImpl(
    private val mapper: SeriesDetailResponseMapper,
    private val seriesDao: CrunchySeriesDao,
    private val endpoint: CrunchySeriesEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    private val cache: SeriesCacheHelper,
    supportDispatchers: SupportDispatchers
) : SeriesDetailSource(supportDispatchers) {

    override val observable = flow {
        val localSource = seriesDao.findBySeriesIdFlow(query.seriesId)
        val result = localSource.filterNotNull().map {
            SeriesEntityConverter.convertFrom(it)
        }
        emitAll(result)
    }

    override suspend fun browseSeries(callback: RequestCallback) {
        if (cache.shouldRefreshSeries(query.seriesId)) {
            val differed = async {
                endpoint.getSeriesInfo(
                    seriesId = query.seriesId
                )
            }

            val controller =
                mapper.controller(
                    dispatchers,
                    OnlineControllerPolicy.create(
                        supportConnectivity
                    )
                )

            val result = controller.invoke(differed, callback)
            if (result != null)
                cache.updateLastRequest(result.id)
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            withContext(context) {
                val seriesId = query.seriesId
                seriesDao.clearTableById(seriesId)
                cache.invalidateLastRequest(seriesId)
            }
        }
    }
}