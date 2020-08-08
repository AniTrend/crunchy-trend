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

package co.anitrend.support.crunchyroll.data.catalog.source

import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.arch.extension.util.DEFAULT_PAGE_SIZE
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyModelField
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.batch.source.contract.BatchSource
import co.anitrend.support.crunchyroll.data.batch.usecase.model.CrunchyBatchQuery
import co.anitrend.support.crunchyroll.data.catalog.datasource.local.CrunchyCatalogDao
import co.anitrend.support.crunchyroll.data.catalog.helper.CatalogCacheHelper
import co.anitrend.support.crunchyroll.data.catalog.mapper.CatalogResponseMapper
import co.anitrend.support.crunchyroll.data.catalog.source.contract.CatalogSource
import co.anitrend.support.crunchyroll.data.catalog.transformer.CrunchyCatalogTransformer
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import timber.log.Timber

internal class CatalogSourceImpl(
    private val catalogDao: CrunchyCatalogDao,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    private val batchSource: BatchSource,
    private val mapper: CatalogResponseMapper,
    private val cache: CatalogCacheHelper,
    supportDispatchers: SupportDispatchers
) : CatalogSource(supportDispatchers), KoinComponent {

    // Static id for catalog id, for persistent
    private val requestId: Long = 100

    override val observable = flow {
        val catalogFlow = catalogDao.findAllFlow()

        emitAll(
            catalogFlow.mapNotNull {
                CrunchyCatalogTransformer.transform(it)
            }
        )
    }

    override suspend fun getCatalog(callback: RequestCallback) {
        if (cache.shouldUpdateCatalog(requestId, catalogDao.count())) {
            val requests = CrunchySeriesCatalogFilter.values().map {
                CrunchyBatchQuery(
                    method_version = BuildConfig.apiExtension,
                    api_method = "list_series",
                    params = mapOf(
                        "media_type" to "anime",
                        "filter" to it.attribute,
                        "limit" to DEFAULT_PAGE_SIZE,
                        "offset" to "0",
                        "fields" to CrunchyModelField.seriesFields
                    )
                )
            }

            batchSource.getBatchOfSeries(requests, callback)
                .filterNotNull()
                .onEach { results ->
                    mapper.onResponseMapFrom(results)

                    if (results.isNotEmpty()) {
                        cache.updateLastRequest(requestId)
                        Timber.tag(moduleTag)
                            .v("Saving request: $requestId to cache log, upon success")
                    }
                }
                .collect()
        } else
            Timber.tag(moduleTag).v("Skipping request due to expiry time not satisfied $requestId")
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            withContext(context) {
                cache.invalidateLastRequest(requestId)
                catalogDao.clearTable()
            }
        }
    }
}