package co.anitrend.support.crunchyroll.data.api.endpoint

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.EndpointFactory
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyAuthUser
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.auth.model.contract.ICrunchySessionUser
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import retrofit2.Response
import retrofit2.http.*

interface CrunchyAuthEndpoint {

    @POST("/login.${BuildConfig.apiExtension}.json")
    suspend fun loginUser(
        @Field("account") account: String?,
        @Field("password") password: String?,
        @Field("session_id") sessionId: String?
    ): Response<CrunchyContainer<CrunchyAuthUser>>

    @POST("/logout.${BuildConfig.apiExtension}.json")
    suspend fun logoutUser(
        @Field("session_id") sessionId: String?
    ): Response<CrunchyContainer<CrunchyAuthUser>>

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