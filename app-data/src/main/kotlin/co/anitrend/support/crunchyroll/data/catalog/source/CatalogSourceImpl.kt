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

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OfflineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.catalog.datasource.local.CrunchyCatalogDao
import co.anitrend.support.crunchyroll.data.catalog.mapper.CatalogResponseMapper
import co.anitrend.support.crunchyroll.data.catalog.source.contract.CatalogSource
import co.anitrend.support.crunchyroll.data.catalog.transformer.CrunchyCatalogTransformer
import co.anitrend.support.crunchyroll.data.series.datasource.remote.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.domain.catalog.models.CrunchyCatalogQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.core.parameter.parametersOf

internal class CatalogSourceImpl(
    private val catalogDao: CrunchyCatalogDao,
    private val endpoint: CrunchySeriesEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    supportDispatchers: SupportDispatchers
) : CatalogSource(supportDispatchers), KoinComponent {

    override suspend fun getCatalog() {
        val deferred = async {
            endpoint.getSeriesList(
                offset = 0,
                filter = query.catalogFilter.attribute
            )
        }

        /** Not injecting via the constructor as we depending on [query] to be provided */
        val mapper = get<CatalogResponseMapper>{
            parametersOf(query.catalogFilter)
        }

        val controller =
            mapper.controller(
                dispatchers,
                OfflineControllerPolicy.create()
            )

        controller(deferred, networkState)

    }

    override val observable =
        object :ISourceObservable<Nothing?, CrunchyCatalogWithSeries> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: Nothing?): LiveData<CrunchyCatalogWithSeries> {
                val catalogFlow = catalogDao.findMatchingFlow(
                    query.catalogFilter
                )

                @Suppress("EXPERIMENTAL_API_USAGE")
                return catalogFlow.mapNotNull {
                    CrunchyCatalogTransformer.transform(it)
                }.flowOn(supportDispatchers.io).asLiveData()
            }
        }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            catalogDao.clearTableMatching(query.catalogFilter)
        }
    }
}