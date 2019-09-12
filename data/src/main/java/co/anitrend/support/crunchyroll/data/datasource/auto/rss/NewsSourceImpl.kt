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
import co.anitrend.support.crunchyroll.data.datasource.auto.rss.contract.NewsSource
import co.anitrend.support.crunchyroll.data.datasource.local.rss.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.mapper.rss.NewsResponseMapper
import co.anitrend.support.crunchyroll.data.transformer.NewsTransformer
import co.anitrend.support.crunchyroll.domain.entities.query.rss.RssQuery
import co.anitrend.support.crunchyroll.domain.entities.result.rss.News
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NewsSourceImpl(
    private val responseMapper: NewsResponseMapper,
    private val endpoint: CrunchyFeedEndpoint,
    private val dao: CrunchyRssNewsDao
) : NewsSource() {

    private lateinit var rssQuery: RssQuery

    override val newsObservable =
        object : ISourceObservable<RssQuery, PagedList<News>> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: RssQuery): LiveData<PagedList<News>> {
                rssQuery = parameter

                val localSource = dao.findByAllFactory()

                val result = localSource.map {
                    NewsTransformer.transform(it)
                }

                return Transformations.distinctUntilChanged(
                    result.toLiveData(
                        config = SupportDataKeyStore.PAGING_CONFIGURATION,
                        boundaryCallback = this@NewsSourceImpl
                    )
                )
            }
        }

    override suspend fun getNewsCatalogue(callback: PagingRequestHelper.Request.Callback) {
        val deferred = async {
            endpoint.getMediaNews(rssQuery.language)
        }

        responseMapper.news(deferred, callback)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        dao.clearTable()
    }
}