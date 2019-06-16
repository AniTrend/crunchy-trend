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

package co.anitrend.support.crunchyroll.data.usecase.series

import androidx.annotation.StringDef
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchySeriesDao
import co.anitrend.support.crunchyroll.data.source.series.CrunchySeriesDataSource
import co.anitrend.support.crunchyroll.data.usecase.contract.IMappable
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.usecase.coroutine.ISupportCoroutineUseCase

class CrunchySeriesUseCase(
    private val seriesEndpoint: CrunchySeriesEndpoint,
    private val seriesDao: CrunchySeriesDao
) : ISupportCoroutineUseCase<CrunchySeriesUseCase.Payload, NetworkState> {

    /**
     * Solves a given use case in the implementation target
     *
     * @param param input for solving a given use case
     */
    override suspend fun invoke(param: Payload): NetworkState {
        val dataSource = CrunchySeriesDataSource(
            seriesDao = seriesDao,
            seriesEndpoint = seriesEndpoint,
            payload = param
        )
        return dataSource()
    }

    data class Payload(
        @RequestType
        val seriesRequestType: String,
        val seriesId: Int,
        val query: String?
    ) : IMappable {

        override fun toMap() = mapOf(
            "series_id" to seriesId,
            "q" to query
        )

        @StringDef(
            RequestType.DETAILS,
            RequestType.SEARCH
        )
        annotation class RequestType {
            companion object {
                const val DETAILS = "DETAILS"
                const val SEARCH = "SEARCH"
            }
        }
    }
}