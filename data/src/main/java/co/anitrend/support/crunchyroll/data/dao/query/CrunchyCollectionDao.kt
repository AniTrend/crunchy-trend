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

package co.anitrend.support.crunchyroll.data.dao.query

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import io.wax911.support.data.dao.ISupportQuery

@Dao
abstract class CrunchyCollectionDao : ISupportQuery<CrunchyCollection> {

    @Query("delete from CrunchyCollection")
    abstract suspend fun clearTable()


    @Query("select * from CrunchyCollection where collection_id = :collectionId")
    abstract fun findByCollectionIdX(
        collectionId: Int
    ): LiveData<CrunchyCollection?>

    @Query("select * from CrunchyCollection where collection_id = :collectionId")
    abstract fun findByCollectionId(
        collectionId: Int
    ): CrunchyCollection?



    @Query("select * from CrunchyCollection where series_id = :seriesId")
    abstract suspend fun findBySeriesId(
        seriesId: Int
    ): List<CrunchyCollection>

    @Query("select * from CrunchyCollection where series_id = :seriesId")
    abstract fun findBySeriesIdX(
        seriesId: Int
    ): LiveData<List<CrunchyCollection>>

    @Query("select * from CrunchyCollection where series_id = :seriesId")
    abstract fun findBySeriesIdFactory(
        seriesId: Int
    ): DataSource.Factory<Int, CrunchyCollection>
}