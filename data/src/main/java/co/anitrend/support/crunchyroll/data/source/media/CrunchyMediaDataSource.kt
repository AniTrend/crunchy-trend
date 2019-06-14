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

package co.anitrend.support.crunchyroll.data.source.media

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.mapper.media.CrunchyMediaMapper
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import co.anitrend.support.crunchyroll.data.repository.media.CrunchyMediaRequestType
import io.wax911.support.data.source.SupportPagingDataSource
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.util.SupportDataKeyStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.inject

class CrunchyMediaDataSource(
    parentJob: Job,
    private val mediaEndpoint: CrunchyMediaEndpoint,
    private val mediaDao: CrunchyMediaDao,
    private val bundle: Bundle
) : SupportPagingDataSource<CrunchyMedia>(parentJob) {

    /**
     * Invokes dynamic requests which can be consumed which can be mapped
     * to a destination source on success
     */
    override fun startRequestForType(callback: PagingRequestHelper.Request.Callback) {
        val futureResponse = async {
            mediaEndpoint.getMediaList(
                collectionId = bundle.getInt(CrunchyMediaRequestType.collectionId),
                offset = supportPagingHelper.pageOffset,
                limit = supportPagingHelper.pageSize
            )
        }

        val mapper = CrunchyMediaMapper(
            parentJob = supervisorJob,
            mediaDao = mediaDao,
            pagingRequestHelper = callback
        )


        launch {
            mapper.handleResponse(futureResponse)
        }
    }

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        pagingRequestHelper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            startRequestForType(it)
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
    override fun onItemAtEndLoaded(itemAtEnd: CrunchyMedia) {
        pagingRequestHelper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            supportPagingHelper.onPageNext()
            startRequestForType(it)
        }
    }

    /**
     * Clears all the data in a database table which will assure that
     * and refresh the backing storage medium with new network data
     */
    override fun refreshOrInvalidate() {
        super.refreshOrInvalidate()
        launch {
            mediaDao.clearTable()
        }
    }

    val media = object : ISourceObservable<PagedList<CrunchyMedia>> {

        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param bundle request params, implementation is up to the developer
         */
        override fun observerOnLiveDataWith(bundle: Bundle): LiveData<PagedList<CrunchyMedia>> {
            val dataSource = mediaDao.findByCollectionIdFactory(
                collectionId = bundle.getInt(CrunchyMediaRequestType.collectionId)
            )
            return dataSource.toLiveData(
                config = SupportDataKeyStore.PAGING_CONFIGURATION,
                boundaryCallback = this@CrunchyMediaDataSource
            )
        }
    }
}