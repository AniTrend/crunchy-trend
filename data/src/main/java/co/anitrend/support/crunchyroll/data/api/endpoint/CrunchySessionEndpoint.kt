package co.anitrend.support.crunchyroll.data.api.endpoint

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.EndpointFactory
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrunchySessionEndpoint {

    @GET("/start_session")
    suspend fun startUnblockedSession(
        @Query("auth") auth: String?,
        @Query("user_id") userId: Long?,
        @Query("version") version: String = BuildConfig.apiVersion
    ): Response<CrunchyContainer<CrunchySession>>

    @GET("/start_session")
    suspend fun startSession(

    ): Response<CrunchyContainer<CrunchySessionCore>>

    companion object : EndpointFactory<CrunchySessionEndpoint>(
        BuildConfig.apiAuthUrl,
        CrunchySessionEndpoint::class.java,
        false
    )
}