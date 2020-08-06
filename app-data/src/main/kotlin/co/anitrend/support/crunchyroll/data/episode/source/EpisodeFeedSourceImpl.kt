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

package co.anitrend.support.crunchyroll.data.episode.source

import androidx.lifecycle.liveData
import androidx.paging.toLiveData
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.util.PAGING_CONFIGURATION
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.episode.datasource.local.CrunchyRssEpisodeDao
import co.anitrend.support.crunchyroll.data.episode.datasource.remote.CrunchyEpisodeFeedEndpoint
import co.anitrend.support.crunchyroll.data.episode.mapper.EpisodeFeedResponseMapper
import co.anitrend.support.crunchyroll.data.episode.source.contract.EpisodeFeedSource
import co.anitrend.support.crunchyroll.data.episode.transformer.EpisodeFeedTransformer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

internal class EpisodeFeedSourceImpl(
    private val mapper: EpisodeFeedResponseMapper,
    private val endpoint: CrunchyEpisodeFeedEndpoint,
    private val dao: CrunchyRssEpisodeDao,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    supportDispatchers: SupportDispatchers
) : EpisodeFeedSource(supportDispatchers) {

    override val observable = liveData {
        val localSource = dao.findByAllFactory()

        val result = localSource.map {
            EpisodeFeedTransformer.transform(it)
        }

        emitSource(
            result.toLiveData(
                config = PAGING_CONFIGURATION,
                boundaryCallback = this@EpisodeFeedSourceImpl
            )
        )
    }

    override suspend fun getMediaListingsCatalogue(callback: RequestCallback) {
        val deferred = async {
            endpoint.getLatestMediaFeed(query.language)
        }

        val controller =
            mapper.controller(
                dispatchers,
                OnlineControllerPolicy.create(
                    supportConnectivity
                )
            )

        controller.episode(deferred, callback)
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