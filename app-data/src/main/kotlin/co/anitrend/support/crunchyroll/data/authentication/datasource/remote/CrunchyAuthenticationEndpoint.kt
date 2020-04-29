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

package co.anitrend.support.crunchyroll.data.authentication.datasource.remote

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.arch.JSON
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.authentication.model.CrunchyLoginModel
import co.anitrend.support.crunchyroll.data.session.model.CrunchySessionModel
import retrofit2.Response
import retrofit2.http.*

internal interface CrunchyAuthenticationEndpoint {

    @JSON
    @FormUrlEncoded
    @POST("/login.${BuildConfig.apiExtension}.json")
    suspend fun loginUser(
        @Field("account") account: String,
        @Field("password") password: String,
        @Field("session_id") sessionId: String?
    ): Response<CrunchyContainer<CrunchyLoginModel>>

    @JSON
    @FormUrlEncoded
    @POST("/logout.${BuildConfig.apiExtension}.json")
    suspend fun logoutUser(
        @Field("session_id") sessionId: String?
    ): Response<CrunchyContainer<Any>>

    @JSON
    @GET("/start_session.${BuildConfig.apiExtension}.json")
    suspend fun startNormalSession(
        @Query("access_token") accessToken: String = BuildConfig.clientToken,
        @Query("device_type") deviceType: String,
        @Query("device_id") deviceId: String,
        @Query("auth") auth: String,
        @Query("version") version: String = BuildConfig.apiVersion
    ): Response<CrunchyContainer<CrunchySessionModel>>
}