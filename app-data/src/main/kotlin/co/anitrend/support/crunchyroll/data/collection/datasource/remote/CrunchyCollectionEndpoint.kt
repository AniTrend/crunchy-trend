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

package co.anitrend.support.crunchyroll.data.collection.datasource.remote

import co.anitrend.arch.extension.util.DEFAULT_PAGE_SIZE
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.arch.JSON
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyModelField
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.collection.model.CrunchyCollectionModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CrunchyCollectionEndpoint {

    @JSON
    @GET("/list_collections.${BuildConfig.apiExtension}.json")
    suspend fun getCollections(
        @Query("series_id") seriesId: Long,
        @Query("offset") offset: Int,
        @Query("fields") fields: String = CrunchyModelField.collectionFields,
        @Query("limit") limit: Int = DEFAULT_PAGE_SIZE
    ) : Response<CrunchyContainer<List<CrunchyCollectionModel>>>
}