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

import android.os.Bundle
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.arch.source.CrunchyWorkerDataSource
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.mapper.series.CrunchySeriesMapper
import co.anitrend.support.crunchyroll.data.mapper.series.CrunchySeriesSearchMapper
import co.anitrend.support.crunchyroll.data.repository.series.CrunchySeriesRequestType
import io.wax911.support.data.model.NetworkState
import io.wax911.support.extension.util.SupportExtKeyStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import org.koin.core.inject
import timber.log.Timber

class CrunchySeriesDataSource(
    parentCoroutineJob: Job? = null,
    private val seriesEndpoint: CrunchySeriesEndpoint
) : CrunchyWorkerDataSource(parentCoroutineJob) {

    override val databaseHelper by inject<CrunchyDatabase>()

    /**
     * Handles the requesting data from a the network source and informs the
     * network state that it is in the loading state
     *
     * @param bundle request parameters or more
     */
    override suspend fun startRequestForType(bundle: Bundle?): NetworkState {
        return when (val requestType = bundle?.getString(SupportExtKeyStore.arg_request_type)) {
            CrunchySeriesRequestType.SEARCH -> searchForSeries(bundle)
            CrunchySeriesRequestType.DETAILS -> fetchSeriesDetails(bundle)
            else -> {
                Timber.tag(moduleTag).w("Unable to identify requestType: $requestType")
                NetworkState.error("Unable to identify requestType: $requestType")
            }
        }
    }

    /**
     * Clears all the data in a database table which will assure that
     * and refresh the backing storage medium with new network data
     *
     * @param bundle the request request parameters to use
     */
    override suspend fun refreshOrInvalidate(bundle: Bundle?): NetworkState {
        databaseHelper.crunchySeriesDao().clearTable()
        return startRequestForType(bundle)
    }

    private suspend fun searchForSeries(bundle: Bundle): NetworkState {
        val futureResponse  = async {
            seriesEndpoint.getSeriesAutoComplete(
                offset = 0,
                limit = 100,
                query = bundle.getString(CrunchySeriesRequestType.seriesSearchQuery)
            )
        }

        val mapper = CrunchySeriesSearchMapper(
            parentJob = supervisorJob,
            seriesDao = databaseHelper.crunchySeriesDao()
        )

        return mapper.handleResponse(futureResponse)

    }

    private suspend fun fetchSeriesDetails(bundle: Bundle): NetworkState {
        val futureResponse  = async {
            seriesEndpoint.getSeriesInfo(
                bundle.getInt(CrunchySeriesRequestType.seriesId)
            )
        }

        val mapper = CrunchySeriesMapper(
            parentJob = supervisorJob,
            seriesDao = databaseHelper.crunchySeriesDao()
        )

        return mapper.handleResponse(futureResponse)
    }
}