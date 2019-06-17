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

package co.anitrend.support.crunchyroll.data.api.endpoint.json

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.contract.JSON
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.EndpointFactory
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import io.wax911.support.extension.util.SupportExtKeyStore
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CrunchyCollectionEndpoint {

    @JSON
    @GET("/list_collections.${BuildConfig.apiVersion}.json")
    suspend fun getCollections(
        @Query("series_id") seriesId: Int?,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int = SupportExtKeyStore.pagingLimit
    ) : Response<CrunchyContainer<List<CrunchyCollection>>>

    companion object : EndpointFactory<CrunchyCollectionEndpoint>(
        endpoint = CrunchyCollectionEndpoint::class,
        url = BuildConfig.apiUrl
    )
}