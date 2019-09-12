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

package co.anitrend.support.crunchyroll.data.datasource.local.api

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import co.anitrend.arch.data.dao.ISupportQuery

@Dao
interface CrunchyCollectionDao : ISupportQuery<CrunchyCollection> {

    @Query("delete from CrunchyCollection")
    suspend fun clearTable()


    @Query("select * from CrunchyCollection where collection_id = :collectionId")
    fun findByCollectionIdX(
        collectionId: Int
    ): LiveData<CrunchyCollection?>

    @Query("select * from CrunchyCollection where collection_id = :collectionId")
    fun findByCollectionId(
        collectionId: Int
    ): CrunchyCollection?



    @Query("select * from CrunchyCollection where series_id = :seriesId")
    suspend fun findBySeriesId(
        seriesId: Int
    ): List<CrunchyCollection>

    @Query("select * from CrunchyCollection where series_id = :seriesId")
    fun findBySeriesIdX(
        seriesId: Int
    ): LiveData<List<CrunchyCollection>>

    @Query("select * from CrunchyCollection where series_id = :seriesId")
    fun findBySeriesIdFactory(
        seriesId: Int
    ): DataSource.Factory<Int, CrunchyCollection>
}