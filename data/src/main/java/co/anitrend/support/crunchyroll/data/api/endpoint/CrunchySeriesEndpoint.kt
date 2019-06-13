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

package co.anitrend.support.crunchyroll.data.api.endpoint

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.EndpointFactory
import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaTypeContract
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import co.anitrend.support.crunchyroll.data.model.series.CrunchySeries
import io.wax911.support.extension.util.SupportExtKeyStore
import okhttp3.Interceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrunchySeriesEndpoint {

    @GET("/info.${BuildConfig.apiVersion}.json")
    suspend fun getSeriesInfo(
        @Query("series_id") seriesId: Long
    ) : Response<CrunchyContainer<CrunchySeries>>

    @GET("/autocomplete.${BuildConfig.apiVersion}.json")
    suspend fun getSeriesAutoComplete(
        @Query("q") query: String?,
        @Query("media_types") mediaTypes: String = CrunchyMediaTypeContract.ANIME,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = SupportExtKeyStore.pagingLimit
    ) : Response<CrunchyContainer<List<CrunchySeries>>>

    companion object : EndpointFactory<CrunchySeriesEndpoint>(
        BuildConfig.apiUrl,
        CrunchySeriesEndpoint::class.java
    )
}