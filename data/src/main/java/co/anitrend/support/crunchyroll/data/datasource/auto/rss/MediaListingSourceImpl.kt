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

package co.anitrend.support.crunchyroll.data.datasource.auto.rss

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.util.SupportDataKeyStore
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyFeedEndpoint
import co.anitrend.support.crunchyroll.data.datasource.auto.rss.contract.MediaListingSource
import co.anitrend.support.crunchyroll.data.datasource.local.rss.CrunchyRssMediaDao
import co.anitrend.support.crunchyroll.data.mapper.rss.MediaListingResponseMapper
import co.anitrend.support.crunchyroll.data.transformer.MediaListingTransformer
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import co.anitrend.support.crunchyroll.domain.entities.query.rss.RssQuery
import co.anitrend.support.crunchyroll.domain.entities.result.rss.MediaListing
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class MediaListingSourceImpl(
    private val responseMapper: MediaListingResponseMapper,
    private val endpoint: CrunchyFeedEndpoint,
    private val dao: CrunchyRssMediaDao,
    private val settings: CrunchySettings
) : MediaListingSource() {

    private lateinit var rssQuery: RssQuery

    override val mediaListingsObservable =
        object : ISourceObservable<RssQuery, PagedList<MediaListing>> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: RssQuery): LiveData<PagedList<MediaListing>> {
                rssQuery = parameter

                val locale = Locale.getDefault()
                val hasPremiumAccess = settings.hasAccessToPremium
                val localSource = dao.findByAllFactory()

                val result = localSource.map {
                    MediaListingTransformer.transform(it, locale, hasPremiumAccess)
                }

                return result.toLiveData(
                    config = SupportDataKeyStore.PAGING_CONFIGURATION,
                    boundaryCallback = this@MediaListingSourceImpl
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