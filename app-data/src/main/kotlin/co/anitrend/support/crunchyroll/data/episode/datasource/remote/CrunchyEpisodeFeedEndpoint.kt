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

package co.anitrend.support.crunchyroll.data.episode.datasource.remote

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.contract.XML
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.CrunchyEndpointFactory
import co.anitrend.support.crunchyroll.data.rss.core.CrunchyRssEpisodeContainer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CrunchyEpisodeFeedEndpoint {

    @XML
    @GET("/crunchyroll/rss/anime/popular")
    suspend fun getPopularMediaFeed(
        @Query("locale") crunchyLocale: String
    ): Response<CrunchyRssEpisodeContainer>

    @XML
    @GET("/crunchyroll/rss/anime")
    suspend fun getLatestMediaFeed(
        @Query("locale") crunchyLocale: String
    ): Response<CrunchyRssEpisodeContainer>

    companion object : CrunchyEndpointFactory<CrunchyEpisodeFeedEndpoint>(
        url = BuildConfig.apiFeed,
        injectInterceptor = false,
        endpoint = CrunchyEpisodeFeedEndpoint::class
    )
}