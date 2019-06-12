package co.anitrend.support.crunchyroll.data.api.endpoint

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.EndpointFactory
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import co.anitrend.support.crunchyroll.data.model.core.CrunchyLocale
import okhttp3.Interceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import retrofit2.http.GET

interface CrunchyCoreEndpoint {

    @GET("/list_locales.${BuildConfig.apiExtension}.json")
    suspend fun fetchLocales(

    ) : Response<CrunchyContainer<List<CrunchyLocale>>>

    companion object : EndpointFactory<CrunchyCoreEndpoint>(
        BuildConfig.apiUrl,
        CrunchyCoreEndpoint::class.java
    )
}