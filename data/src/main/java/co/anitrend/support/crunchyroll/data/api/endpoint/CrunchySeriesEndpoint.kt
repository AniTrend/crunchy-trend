package co.anitrend.support.crunchyroll.data.api.endpoint

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.EndpointFactory
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import co.anitrend.support.crunchyroll.data.model.series.CrunchySeries
import okhttp3.Interceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrunchySeriesEndpoint {

    @GET("/info.${BuildConfig.apiVersion}.json")
    suspend fun getSeriesInfo(
        @Query("series_id") mediaId: Long
    ) : Response<CrunchyContainer<CrunchySeries>>

    companion object : EndpointFactory<CrunchySeriesEndpoint>(
        BuildConfig.apiUrl,
        CrunchySeriesEndpoint::class.java
    )
}