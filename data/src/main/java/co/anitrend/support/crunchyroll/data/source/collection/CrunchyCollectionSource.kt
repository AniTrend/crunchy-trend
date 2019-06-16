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

import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyCollectionEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchyCollectionDao
import co.anitrend.support.crunchyroll.data.mapper.collection.CrunchyCollectionMapper
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import co.anitrend.support.crunchyroll.data.usecase.series.CrunchySeriesUseCase
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.source.coroutine.SupportCoroutineDataSource
import kotlinx.coroutines.async

class CrunchyCollectionSource(
    private val collectionEndpoint: CrunchyCollectionEndpoint,
    private val collectionDao: CrunchyCollectionDao,
    private val payload: CrunchySeriesUseCase.Payload
) : SupportCoroutineDataSource() {

    /**
     * Handles the requesting data from a the network source and return
     * [NetworkState] to the caller after execution.
     *
     * In this context the super.invoke() method will allow a retry action to be set
     */
    override suspend fun invoke(): NetworkState {
        super.invoke()
        val futureResponse = async {
            collectionEndpoint.getCollections(
                seriesId = payload.seriesId,
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
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        collectionDao.clearTable()
    }

    val collection =
        object : ISourceObservable<List<CrunchyCollection>, CrunchySeriesUseCase.Payload> {
        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param parameter parameters, implementation is up to the developer
         */
        override fun invoke(parameter: CrunchySeriesUseCase.Payload): LiveData<List<CrunchyCollection>> {
            return collectionDao.findBySeriesIdX(payload.seriesId)
        }
    }
}