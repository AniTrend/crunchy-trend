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

package co.anitrend.support.crunchyroll.data.source.series

import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchySeriesDao
import co.anitrend.support.crunchyroll.data.mapper.series.CrunchySeriesMapper
import co.anitrend.support.crunchyroll.data.mapper.series.CrunchySeriesSearchMapper
import co.anitrend.support.crunchyroll.data.usecase.series.CrunchySeriesUseCase
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.source.coroutine.SupportCoroutineDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import timber.log.Timber

class CrunchySeriesDataSource(
    parentCoroutineJob: Job? = null,
    private val seriesEndpoint: CrunchySeriesEndpoint,
    private val seriesDao: CrunchySeriesDao,
    private val payload: CrunchySeriesUseCase.Payload
) : SupportCoroutineDataSource(parentCoroutineJob) {

    /**
     * Handles the requesting data from a the network source and return
     * [NetworkState] to the caller after execution.
     *
     * In this context the super.invoke() method will allow a retry action to be set
     */
    override suspend fun invoke(): NetworkState {
        super.invoke()
        return when (val requestType = payload.seriesRequestType) {
            CrunchySeriesUseCase.Payload.RequestType.SEARCH ->
                searchForSeries()
            CrunchySeriesUseCase.Payload.RequestType.DETAILS ->
                fetchSeriesDetails()
            else -> {
                Timber.tag(moduleTag).w("Unable to identify sessionType: $requestType")
                NetworkState.error("Unable to identify sessionType: $requestType")
            }
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        seriesDao.clearTable()
    }

    private suspend fun searchForSeries(): NetworkState {
        val futureResponse  = async {
            seriesEndpoint.getSeriesAutoComplete(
                offset = 0,
                limit = 100,
                query = payload.query
            )
        }

        val mapper = CrunchySeriesSearchMapper(
            parentJob = supervisorJob,
            seriesDao = seriesDao
        )

        return mapper.handleResponse(futureResponse)

    }

    private suspend fun fetchSeriesDetails(): NetworkState {
        val futureResponse  = async {
            seriesEndpoint.getSeriesInfo(
                seriesId = payload.seriesId
            )
        }

        val mapper = CrunchySeriesMapper(
            parentJob = supervisorJob,
            seriesDao = seriesDao
        )

        return mapper.handleResponse(futureResponse)
    }
}