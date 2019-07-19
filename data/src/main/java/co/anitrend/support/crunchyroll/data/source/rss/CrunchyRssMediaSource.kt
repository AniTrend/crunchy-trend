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

package co.anitrend.support.crunchyroll.data.source.rss

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyFeedEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.rss.CrunchyRssMediaDao
import co.anitrend.support.crunchyroll.data.mapper.rss.CrunchyRssMediaMapper
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssMedia
import co.anitrend.support.crunchyroll.data.usecase.rss.CrunchyRssMediaUseCase
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.source.paging.SupportPagingDataSource
import io.wax911.support.data.util.SupportDataKeyStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CrunchyRssMediaSource(
    parentCoroutineJob: Job? = null,
    private val rssMediaDao: CrunchyRssMediaDao,
    private val rssCrunchyEndpoint: CrunchyEndpoint,
    private val rssFeedCrunchyEndpoint: CrunchyFeedEndpoint,
    private val payload: CrunchyRssMediaUseCase.Payload
) : SupportPagingDataSource<CrunchyRssMedia>(parentCoroutineJob) {

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        pagingRequestHelper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            invoke(it)
        }
    }

    /**
     * Called when the item at the end of the PagedList has been loaded, and access has
     * occurred within [Config.prefetchDistance] of it.
     *
     *
     * No more data will be appended to the PagedList after this item.
     *
     * @param itemAtEnd The first item of PagedList
     */
    override fun onItemAtEndLoaded(itemAtEnd: CrunchyRssMedia) {
        pagingRequestHelper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            if (supportPagingHelper.page <= 1) {
                supportPagingHelper.onPageNext()
                invoke(it)
            }
        }
    }

    private fun getByMediaSlugAsync() = async {
        rssCrunchyEndpoint.getMediaItemsBySlug(
            mediaSlug = payload.seriesSlug,
            crunchyLocale = payload.locale
        )
    }


    private fun getAllLatestAsync() = async {
        rssFeedCrunchyEndpoint.getLatestMediaFeed(
            crunchyLocale = payload.locale
        )
    }

    /**
     * Dispatches work for the paging data source to respective workers or mappers
     * that publish the result to any [androidx.lifecycle.LiveData] observers
     *
     * @see networkState
     */
    override fun invoke(callback: PagingRequestHelper.Request.Callback) {
        val futureResponse = when (payload.mediaRssRequestType) {
            CrunchyRssMediaUseCase.Payload.RequestType.GET_BY_SERIES_SLUG ->
                getByMediaSlugAsync()
            else ->
                getAllLatestAsync()
        }

        val mapper = CrunchyRssMediaMapper(
            parentJob = supervisorJob,
            crunchyRssMediaDao = rssMediaDao,
            pagingRequestHelper = callback
        )

        launch {
            mapper.handleResponseMedia(futureResponse)
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override fun clearDataSource() {
        launch {
            rssMediaDao.clearTable()
        }
    }

    val media =
        object : ISourceObservable<PagedList<CrunchyRssMedia>, CrunchyRssMediaUseCase.Payload> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter parameters, implementation is up to the developer
             */
            override fun invoke(parameter: CrunchyRssMediaUseCase.Payload): LiveData<PagedList<CrunchyRssMedia>> {
                val dataSource = when (parameter.mediaRssRequestType) {
                    CrunchyRssMediaUseCase.Payload.RequestType.GET_BY_SERIES_SLUG ->
                        rssMediaDao.findBySlugFactory(
                            seriesSlug = payload.seriesSlug
                        )
                    else ->
                        rssMediaDao.findByAllFactory()
                }
                return dataSource.toLiveData(
                    config = SupportDataKeyStore.PAGING_CONFIGURATION,
                    boundaryCallback = this@CrunchyRssMediaSource
                )
            }
        }
}