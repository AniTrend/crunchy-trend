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

package co.anitrend.support.crunchyroll.data.series.source.search

import androidx.paging.PagedList
import co.anitrend.arch.data.paging.FlowPagedListBuilder
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.model.Request
import co.anitrend.arch.data.util.PAGING_CONFIGURATION
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyPagingConfigHelper
import co.anitrend.support.crunchyroll.data.series.SeriesController
import co.anitrend.support.crunchyroll.data.series.converters.SeriesEntityConverter
import co.anitrend.support.crunchyroll.data.series.datasource.local.CrunchySeriesDao
import co.anitrend.support.crunchyroll.data.series.datasource.remote.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.series.source.search.contract.SeriesSearchSource
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class SeriesSearchSourceImpl(
    private val controller: SeriesController,
    private val seriesDao: CrunchySeriesDao,
    private val endpoint: CrunchySeriesEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    override val dispatcher: ISupportDispatcher
) : SeriesSearchSource() {

    override fun observable(
        searchQuery: CrunchySeriesSearchQuery
    ): Flow<PagedList<CrunchySeries>> {
        // Going to use wild cards here since fts isn't matching well with api results
        val factory = seriesDao.findBySeriesNameWildCardFactory("%${searchQuery.searchTerm}%")
                .map { SeriesEntityConverter.convertFrom(it) }

        return FlowPagedListBuilder(
            dataSourceFactory = factory,
            config = PAGING_CONFIGURATION,
            initialLoadKey = null,
            boundaryCallback = this
        ).buildFlow()
    }

    override suspend fun invoke(
        callback: RequestCallback,
        request: Request,
        model: CrunchySeries?
    ) {
        CrunchyPagingConfigHelper(request, supportPagingHelper) {
            // There is an issue with using count on fts tables, so we'll use the `like` operator
            seriesDao.countBySeriesName("%${query.searchTerm}%")
        }

        val deferred = async {
            endpoint.getSeriesAutoComplete(
                offset = supportPagingHelper.pageOffset,
                limit = supportPagingHelper.pageSize,
                query = query.searchTerm
            )
        }

        controller(deferred, callback)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            withContext(context) {
                seriesDao.clearTableByMatch(query.searchTerm)
            }
        }
    }
}