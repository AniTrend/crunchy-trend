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

package co.anitrend.support.crunchyroll.data.api.endpoint.xml

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.contract.XML
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.EndpointFactory
import co.anitrend.support.crunchyroll.data.model.rss.core.CrunchyRssMediaContainer
import co.anitrend.support.crunchyroll.data.model.rss.core.CrunchyRssNewsContainer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CrunchyEndpoint {

    @XML
    @GET("/{media_slug}.rss")
    suspend fun getMediaItemsBySlug(
        @Path("media_slug") mediaSlug: String?,
        @Query("locale") crunchyLocale: String
    ): Response<CrunchyRssMediaContainer>

    @XML
    @GET("/newsrss")
    suspend fun getSeriesNews(
        @Query("locale") crunchyLocale: String
    ): Response<CrunchyRssNewsContainer>

    companion object : EndpointFactory<CrunchyEndpoint>(
        url = BuildConfig.crunchyUrl,
        injectInterceptor = false,
        endpoint = CrunchyEndpoint::class
    )
}