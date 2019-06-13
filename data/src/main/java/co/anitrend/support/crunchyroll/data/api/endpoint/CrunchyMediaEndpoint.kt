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
import co.anitrend.support.crunchyroll.data.arch.MediaFieldsContract
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import co.anitrend.support.crunchyroll.data.model.stream.CrunchyStreamInfo
import io.wax911.support.extension.util.SupportExtKeyStore
import okhttp3.Interceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrunchyMediaEndpoint {

    @GET("/info.${BuildConfig.apiVersion}.json")
    suspend fun getMediaInfo(
        @Query("collection_id") collectionId: Long,
        @Query("offset") offset: Int,
        @Query("fields") mediaFields: String = MediaFieldsContract.mediaFields,
        @Query("limit") limit: Int = SupportExtKeyStore.pagingLimit
    ) : Response<CrunchyContainer<List<CrunchyMedia>>>

    @GET("/list_media.${BuildConfig.apiVersion}.json")
    suspend fun getMediaList(
        @Query("collection_id") collectionId: Long,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = SupportExtKeyStore.pagingLimit
    ) : Response<CrunchyContainer<List<CrunchyMedia>>>

    @GET("/info.${BuildConfig.apiVersion}.json")
    suspend fun getStreamInfo(
        @Query("media_id") mediaId: Long,
        @Query("fields") mediaFields: String = MediaFieldsContract.streamFields,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = SupportExtKeyStore.pagingLimit
    ) : Response<CrunchyContainer<List<CrunchyStreamInfo>>>


    companion object : EndpointFactory<CrunchyMediaEndpoint>(
        BuildConfig.apiUrl,
        CrunchyMediaEndpoint::class.java
    )
}