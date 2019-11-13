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

package co.anitrend.support.crunchyroll.data.stream.datasource.remote

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.contract.JSON
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.CrunchyEndpointFactory
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyMediaField
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.stream.model.CrunchyStreamInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrunchyStreamEndpoint {

    @JSON
    @GET("/info.${BuildConfig.apiExtension}.json")
    suspend fun getStreamInfo(
        @Query("media_id") mediaId: Int,
        @Query("fields") mediaFields: String = CrunchyMediaField.streamFields
    ) : Response<CrunchyContainer<CrunchyStreamInfo>>


    companion object : CrunchyEndpointFactory<CrunchyStreamEndpoint>(
        endpoint = CrunchyStreamEndpoint::class,
        url = BuildConfig.apiUrl
    )
}