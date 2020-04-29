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

package co.anitrend.support.crunchyroll.data.news.source

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.util.SupportDataKeyStore
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.news.datasource.local.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.news.datasource.remote.CrunchyNewsFeedEndpoint
import co.anitrend.support.crunchyroll.data.news.mapper.NewsResponseMapper
import co.anitrend.support.crunchyroll.data.news.source.contract.NewsSource
import co.anitrend.support.crunchyroll.data.news.transformer.NewsTransformer
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal class NewsSourceImpl(
    private val mapper: NewsResponseMapper,
    private val endpoint: CrunchyNewsFeedEndpoint,
    private val dao: CrunchyRssNewsDao,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    supportDispatchers: SupportDispatchers
) : NewsSource(supportDispatchers) {

    override val newsObservable =
        object : ISourceObservable<Nothing?, PagedList<CrunchyNews>> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: Nothing?): LiveData<PagedList<CrunchyNews>> {
                val localSource = dao.findByAllFactory()

                val result = localSource.map {
                    NewsTransformer.transform(it)
                }

                return result.toLiveData(
                    config = SupportDataKeyStore.PAGING_CONFIGURATION,
                    boundaryCallback = this@NewsSourceImpl
                )
            }
        }

    override suspend fun getNewsCatalogue(callback: PagingRequestHelper.Request.Callback) {
        val deferred = async {
            endpoint.getMediaNews(query.language)
        }

        val controller =
            mapper.controller(
                dispatchers,
                OnlineControllerPolicy.create(
                    supportConnectivity
                )
            )

        controller.news(deferred, callback)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            dao.clearTable()
        }
    }
}