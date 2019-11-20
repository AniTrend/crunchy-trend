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

package co.anitrend.support.crunchyroll.data.series.datasource.remote

import co.anitrend.arch.extension.util.SupportExtKeyStore
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.contract.JSON
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.CrunchyEndpointFactory
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyModelField
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.series.model.CrunchySeriesModel
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchySeriesFilter
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrunchySeriesEndpoint {

    @JSON
    @GET("/info.${BuildConfig.apiExtension}.json")
    suspend fun getSeriesInfo(
        @Query("series_id") seriesId: Long
    ) : Response<CrunchyContainer<CrunchySeriesModel>>

    @JSON
    @GET("/list_series.${BuildConfig.apiExtension}.json")
    suspend fun getSeriesList(
        @Query("filter") filter: String = CrunchySeriesFilter.ALPHA.attribute,
        @Query("media_type") mediaType: String = CrunchyMediaType.anime.name,
        @Query("fields") seriesFields: String = CrunchyModelField.seriesFields,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = SupportExtKeyStore.pagingLimit
    ) : Response<CrunchyContainer<List<CrunchySeriesModel>>>

    @JSON
    @GET("/autocomplete.${BuildConfig.apiExtension}.json")
    suspend fun getSeriesAutoComplete(
        @Query("q") query: String,
        @Query("media_types") mediaTypes: String = CrunchyMediaType.anime.name,
        @Query("fields") seriesFields: String = CrunchyModelField.seriesFields,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = SupportExtKeyStore.pagingLimit
    ) : Response<CrunchyContainer<List<CrunchySeriesModel>>>

    companion object : CrunchyEndpointFactory<CrunchySeriesEndpoint>(
        endpoint = CrunchySeriesEndpoint::class,
        url = BuildConfig.apiUrl
    )
}