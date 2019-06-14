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

package co.anitrend.support.crunchyroll.data.source.collection

import android.os.Bundle
import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyCollectionEndpoint
import co.anitrend.support.crunchyroll.data.arch.source.CrunchyWorkerDataSource
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyCollectionDao
import co.anitrend.support.crunchyroll.data.mapper.collection.CrunchyCollectionMapper
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import co.anitrend.support.crunchyroll.data.repository.series.CrunchySeriesRequestType
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.source.contract.ISourceObservable
import kotlinx.coroutines.async

class CrunchyCollectionSource(
    private val collectionEndpoint: CrunchyCollectionEndpoint,
    private val collectionDao: CrunchyCollectionDao
) : CrunchyWorkerDataSource() {

    /**
     * Handles the requesting data from a the network source and informs the
     * network state that it is in the loading state
     *
     * @param bundle request parameters or more
     */
    override suspend fun startRequestForType(bundle: Bundle?): NetworkState {
        val futureResponse = async {
            collectionEndpoint.getCollections(
                seriesId = bundle?.getInt(CrunchySeriesRequestType.seriesId),
                offset = null,
                limit = 50
            )
        }

        val mapper = CrunchyCollectionMapper(
            parentJob = supervisorJob,
            collectionDao = collectionDao
        )

        return mapper.handleResponse(futureResponse)
    }

    /**
     * Clears all the data in a database table which will assure that
     * and refresh the backing storage medium with new network data
     *
     * @param bundle the request request parameters to use
     */
    override suspend fun refreshOrInvalidate(bundle: Bundle?): NetworkState {
        collectionDao.clearTable()
        return startRequestForType(bundle)
    }

    val collection = object : ISourceObservable<List<CrunchyCollection>> {

        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param bundle request params, implementation is up to the developer
         */
        override fun observerOnLiveDataWith(bundle: Bundle): LiveData<List<CrunchyCollection>> {
            return collectionDao.findBySeriesIdX(bundle.getInt(CrunchySeriesRequestType.seriesId))
        }
    }
}