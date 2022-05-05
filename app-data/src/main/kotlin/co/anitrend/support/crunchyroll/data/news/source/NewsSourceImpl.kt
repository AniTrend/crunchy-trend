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

import androidx.paging.PagedList
import co.anitrend.arch.data.paging.FlowPagedListBuilder
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.util.PAGING_CONFIGURATION
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.news.NewPagedController
import co.anitrend.support.crunchyroll.data.news.datasource.local.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.news.datasource.remote.CrunchyNewsFeedEndpoint
import co.anitrend.support.crunchyroll.data.news.source.contract.NewsSource
import co.anitrend.support.crunchyroll.data.news.transformer.NewsTransformer
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class NewsSourceImpl(
    private val controller: NewPagedController,
    private val endpoint: CrunchyNewsFeedEndpoint,
    private val dao: CrunchyRssNewsDao,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    override val dispatcher: ISupportDispatcher
) : NewsSource() {

    override fun observable(query: RssQuery): Flow<PagedList<CrunchyNews>> {
        val factory = dao.findByAllFactory()
            .map { NewsTransformer.transform(it) }

        return FlowPagedListBuilder(
            dataSourceFactory = factory,
            config = PAGING_CONFIGURATION,
            initialLoadKey = null,
            boundaryCallback = this
        ).buildFlow()
    }

    override suspend fun getNewsCatalogue(callback: RequestCallback) {
        val deferred = async {
            endpoint.getMediaNews(query.language)
        }

        controller(deferred, callback)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            withContext(context) {
                dao.clearTable()
            }
        }
    }
}