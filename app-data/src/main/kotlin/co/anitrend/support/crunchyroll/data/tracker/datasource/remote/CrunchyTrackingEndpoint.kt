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

package co.anitrend.support.crunchyroll.data.tracker.datasource.remote

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.arch.JSON
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyModelField
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.tracker.model.CrunchyQueueModel
import co.anitrend.support.crunchyroll.data.tracker.model.CrunchyRecentlyWatchedModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CrunchyTrackingEndpoint {

    @JSON
    @GET("/add_to_queue.${BuildConfig.apiExtension}.json")
    suspend fun addToQueue(
        @Query("series_id") seriesId: Long
    ) : Response<CrunchyContainer<Any>>

    @JSON
    @GET("/remove_from_queue.${BuildConfig.apiExtension}.json")
    suspend fun removeFromQueue(
        @Query("series_id") seriesId: Long
    ) : Response<CrunchyContainer<Any>>

    @JSON
    @GET("/queue.${BuildConfig.apiExtension}.json")
    suspend fun getQueue(
        @Query("series_id") seriesId: Long,
        @Query("fields") fields: String = CrunchyModelField.queueFields
    ) : Response<CrunchyContainer<List<CrunchyQueueModel>>>

    @JSON
    @GET("/recently_watched.${BuildConfig.apiExtension}.json")
    suspend fun getRecentlyWatched(
        @Query("series_id") seriesId: Long,
        @Query("media_types") mediaType: String,
        @Query("fields") fields: String = CrunchyModelField.recentlyWatchedFields
    ) : Response<CrunchyContainer<List<CrunchyRecentlyWatchedModel>>>

    @JSON
    @GET("/log.${BuildConfig.apiExtension}.json")
    suspend fun savePlayProgress(
        @Query("event") event: String = "playback_status",
        @Query("playhead") playHead: Int,
        @Query("media_id") mediaId: Long
    ) : Response<CrunchyContainer<Any>>
}