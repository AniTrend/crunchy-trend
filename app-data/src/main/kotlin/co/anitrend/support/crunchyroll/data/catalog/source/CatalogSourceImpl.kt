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
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.util.SupportDataKeyStore
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.catalog.datasource.local.CrunchyCatalogDao
import co.anitrend.support.crunchyroll.data.catalog.mapper.CatalogResponseMapper
import co.anitrend.support.crunchyroll.data.catalog.source.contract.CatalogSource
import co.anitrend.support.crunchyroll.data.catalog.transformer.CrunchyCatalogTransformer
import co.anitrend.support.crunchyroll.data.series.datasource.remote.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.domain.catalog.models.CrunchyCatalogQuery
import kotlinx.coroutines.async
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

    private fun getCatalog(
        callback: PagingRequestHelper.Request.Callback,
        param: CrunchyCatalogQuery
    ) {
        val deferred = async {
            endpoint.getSeriesList(
                offset = supportPagingHelper.pageOffset,
                limit = supportPagingHelper.pageSize,
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

            controller(deferred, callback)
        }
    }

    override val catalogObservable =
        object :ISourceObservable<CrunchyCatalogQuery, PagedList<CrunchyCatalogWithSeries>> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: CrunchyCatalogQuery): LiveData<PagedList<CrunchyCatalogWithSeries>> {
                executionTarget = { getCatalog(it, parameter) }

                val localSource = catalogDao.findAllFactory(parameter.catalogFilter)

                val result = localSource.map {
                    CrunchyCatalogTransformer.transform(it)
                }

                return result.toLiveData(
                    config = SupportDataKeyStore.PAGING_CONFIGURATION,
                    boundaryCallback = this@CatalogSourceImpl
                )
            }
        }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        catalogDao.clearTable()
    }
}