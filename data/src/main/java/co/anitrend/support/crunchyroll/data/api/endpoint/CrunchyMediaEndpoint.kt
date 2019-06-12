package co.anitrend.support.crunchyroll.data.api.endpoint

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.EndpointFactory
import co.anitrend.support.crunchyroll.data.arch.MediaFieldsContract
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import co.anitrend.support.crunchyroll.data.model.stream.CrunchyStreamInfo
import okhttp3.Interceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrunchyMediaEndpoint {

    @GET("/info.${BuildConfig.apiVersion}.json")
    suspend fun getMediaInfo(
        @Query("media_id") mediaId: Long,
        @Query("fields") mediaFields: String =
            MediaFieldsContract.ALL.joinToString(separator = ",")
    ) : Response<CrunchyContainer<CrunchyMedia>>

    @GET("/list_media.${BuildConfig.apiVersion}.json")
    suspend fun getMediaList(
        @Query("collection_id") collectionId: Long,
        @Query("limit") limit: Int = 1000,
        @Query("fields") mediaFields: String = MediaFieldsContract.mediaFields
    ) : Response<CrunchyContainer<List<CrunchyMedia>>>

    @GET("/info.${BuildConfig.apiVersion}.json")
    suspend fun getStreamInfo(
        @Query("media_id") mediaId: String,
        @Query("fields") mediaFields: String = MediaFieldsContract.streamFields
    ) : Response<CrunchyContainer<List<CrunchyStreamInfo>>>


    companion object : EndpointFactory<CrunchyMediaEndpoint>(
        BuildConfig.apiUrl,
        CrunchyMediaEndpoint::class.java
    )
}