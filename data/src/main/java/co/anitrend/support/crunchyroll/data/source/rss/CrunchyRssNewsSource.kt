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
import co.anitrend.support.crunchyroll.data.dao.query.rss.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.mapper.rss.CrunchyRssNewsMapper
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssNews
import co.anitrend.support.crunchyroll.data.usecase.rss.contract.IRssUseCase
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.source.paging.SupportPagingDataSource
import io.wax911.support.data.util.SupportDataKeyStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CrunchyRssNewsSource(
    parentJob: Job? = null,
    private val rssNewsDao: CrunchyRssNewsDao,
    private val rssCrunchyEndpoint: CrunchyEndpoint,
    private val payload: IRssUseCase.IRssPayload
) : SupportPagingDataSource<CrunchyRssNews>(parentJob) {

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
    override fun onItemAtEndLoaded(itemAtEnd: CrunchyRssNews) {
        pagingRequestHelper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            if (supportPagingHelper.page <= 1) {
                supportPagingHelper.onPageNext()
                invoke(it)
            }
        }
    }

    /**
     * Dispatches work for the paging data source to respective workers or mappers
     * that publish the result to any [androidx.lifecycle.LiveData] observers
     *
     * @see networkState
     */
    override fun invoke(callback: PagingRequestHelper.Request.Callback) {
        val futureResponse = async {
            rssCrunchyEndpoint.getSeriesNews(
                crunchyLocale = payload.locale
            )
        }

        val mapper = CrunchyRssNewsMapper(
            parentJob = supervisorJob,
            crunchyRssNewsDao = rssNewsDao,
            pagingRequestHelper = callback
        )

        launch {
            mapper.handleResponse(futureResponse)
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override fun clearDataSource() {
        launch {
            rssNewsDao.clearTable()
        }
    }

    val news =
        object : ISourceObservable<PagedList<CrunchyRssNews>, IRssUseCase.IRssPayload> {
        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param parameter parameters, implementation is up to the developer
         */
        override fun invoke(parameter: IRssUseCase.IRssPayload): LiveData<PagedList<CrunchyRssNews>> {
            val dataSource = rssNewsDao.findByAllFactory()
            return dataSource.toLiveData(
                config = SupportDataKeyStore.PAGING_CONFIGURATION,
                boundaryCallback = this@CrunchyRssNewsSource
            )
        }
    }
}