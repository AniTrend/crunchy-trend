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
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.arch.extension.util.SupportExtKeyStore
import co.anitrend.support.crunchyroll.data.arch.extension.controller
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
import org.koin.core.parameter.parametersOf

class CatalogSourceImpl(
    private val catalogDao: CrunchyCatalogDao,
    private val endpoint: CrunchySeriesEndpoint,
    private val supportConnectivity: SupportConnectivity,
    supportDispatchers: SupportDispatchers
) : CatalogSource(supportDispatchers), KoinComponent {

    override fun getCatalog(
        param: CrunchyCatalogQuery
    ): LiveData<CrunchyCatalogWithSeries> {
        retry = { getCatalog(param) }
        val deferred = async {
            endpoint.getSeriesList(
                offset = 0,
                filter = param.catalogFilter.attribute
            )
        }

        /** Not injecting via the constructor as we depending on [param] to be provided */
        val mapper = get<CatalogResponseMapper>{
            parametersOf(param.catalogFilter)
        }

        launch {
            val controller =
                mapper.controller(supportConnectivity, dispatchers)

            controller(deferred, networkState)
        }

        return observable(param)
    }

    override val observable =
        object :ISourceObservable<CrunchyCatalogQuery, CrunchyCatalogWithSeries> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: CrunchyCatalogQuery): LiveData<CrunchyCatalogWithSeries> {

                val catalogFlow = catalogDao.findMatchingFlow(
                    parameter.catalogFilter
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
        catalogDao.clearTable()
    }
}