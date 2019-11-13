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

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.util.SupportDataKeyStore
import co.anitrend.support.crunchyroll.data.episode.source.contract.EpisodeFeedSource
import co.anitrend.support.crunchyroll.data.episode.datasource.local.CrunchyRssEpisodeDao
import co.anitrend.support.crunchyroll.data.episode.mapper.EpisodeFeedResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.transformer.MediaListingTransformer
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.episode.entities.EpisodeFeed
import kotlinx.coroutines.async
import java.util.*

class EpisodeFeedSourceImpl(
    private val responseMapper: EpisodeFeedResponseMapper,
    private val endpoint: CrunchyFeedEndpoint,
    private val dao: CrunchyRssEpisodeDao,
    private val settings: IAuthenticationSettings
) : EpisodeFeedSource() {

    private lateinit var rssQuery: RssQuery

    override val episodeListingsObservable =
        object : ISourceObservable<RssQuery, PagedList<EpisodeFeed>> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: RssQuery): LiveData<PagedList<EpisodeFeed>> {
                rssQuery = parameter

                val locale = Locale.getDefault()
                val hasPremiumAccess = settings.hasAccessToPremium
                val localSource = dao.findByAllFactory()

                val result = localSource.map {
                    MediaListingTransformer.transform(it, locale, hasPremiumAccess)
                }

                return result.toLiveData(
                    config = SupportDataKeyStore.PAGING_CONFIGURATION,
                    boundaryCallback = this@EpisodeFeedSourceImpl
                )
            }
        }

    override suspend fun getMediaListingsCatalogue(callback: PagingRequestHelper.Request.Callback) {
        val deferred = async {
            endpoint.getLatestMediaFeed(rssQuery.language)
        }

        responseMapper.media(deferred, callback)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        dao.clearTable()
    }
}