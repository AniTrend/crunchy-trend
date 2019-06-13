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
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyLogin
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import retrofit2.Response
import retrofit2.http.*

interface CrunchyAuthEndpoint {

    @POST("/login.${BuildConfig.apiExtension}.json")
    suspend fun loginUser(
        @Field("account") account: String?,
        @Field("password") password: String?,
        @Field("session_id") sessionId: String?
    ): Response<CrunchyContainer<CrunchyLogin>>

    @POST("/logout.${BuildConfig.apiExtension}.json")
    suspend fun logoutUser(
        @Field("session_id") sessionId: String?
    ): Response<CrunchyContainer<CrunchyLogin>>

    @GET("/start_session.${BuildConfig.apiExtension}.json")
    suspend fun startNormalSession(
        @Query("access_token") access_token: String = BuildConfig.clientToken,
        @Query("device_type") device_type: String = "com.crunchyroll.windows.desktop",
        @Query("device_id") device_id: String?,
        @Query("auth") auth: String?,
        @Query("version") version: String = BuildConfig.apiVersion
    ): Response<CrunchyContainer<CrunchySession>>

    companion object : EndpointFactory<CrunchySessionEndpoint>(
        BuildConfig.apiUrl,
        CrunchySessionEndpoint::class.java,
        false
    )
}