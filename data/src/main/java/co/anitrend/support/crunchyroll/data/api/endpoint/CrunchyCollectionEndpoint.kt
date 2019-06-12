package co.anitrend.support.crunchyroll.data.api.endpoint

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.EndpointFactory
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import okhttp3.Interceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrunchyCollectionEndpoint {

    @GET("/list_collections.${BuildConfig.apiVersion}.json")
    suspend fun getCollections(
        @Query("series_id") seriesId: Long
    ) : Response<CrunchyContainer<List<CrunchyCollection>>>

    companion object : EndpointFactory<CrunchyCollectionEndpoint>(
        BuildConfig.apiUrl,
        CrunchyCollectionEndpoint::class.java
    )
}