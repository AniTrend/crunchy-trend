/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.data.session.datasource.remote

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.helper.SessionHelper
import co.anitrend.support.crunchyroll.data.arch.JSON
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.session.model.CrunchySessionCoreModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface CrunchyProxySessionEndpoint {

    @JSON
    @POST("/getsession.php")
    @Deprecated("Please use a vpn or something similar for generating tokens")
    suspend fun startCoreSessionProxy(
        @Query("access_token") accessToken: String,
        @Query("device_type") deviceType: String,
        @Query("device_id") deviceId: String = SessionHelper.createDummyDeviceId(),
        @Query("version") version: String = BuildConfig.apiVersion
    ) : Response<CrunchyContainer<CrunchySessionCoreModel>>


    @JSON
    @GET("/start_session")
    suspend fun startCoreSession(
        @Query("version") version: String = BuildConfig.apiVersion
    ): Response<CrunchyContainer<CrunchySessionCoreModel>>
}