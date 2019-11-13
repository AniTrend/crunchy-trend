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

package co.anitrend.support.crunchyroll.data.collection.datasource.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.arch.data.dao.ISupportQuery
import co.anitrend.support.crunchyroll.data.collection.entity.CrunchyCollectionEntity

@Dao
interface CrunchyCollectionDao : ISupportQuery<CrunchyCollectionEntity> {

    @Query("""
        delete from CrunchyCollectionEntity
        """)
    suspend fun clearTable()


    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where collectionId = :collectionId
        """)
    fun findByCollectionIdX(
        collectionId: Int
    ): LiveData<CrunchyCollectionEntity?>

    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where collectionId = :collectionId
        """)
    fun findByCollectionId(
        collectionId: Int
    ): CrunchyCollectionEntity?



    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where seriesId = :seriesId
        """)
    suspend fun findBySeriesId(
        seriesId: Int
    ): List<CrunchyCollectionEntity>

    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where seriesId = :seriesId
        """)
    fun findBySeriesIdX(
        seriesId: Int
    ): LiveData<List<CrunchyCollectionEntity>>

    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where seriesId = :seriesId
        """)
    fun findBySeriesIdFactory(
        seriesId: Int
    ): DataSource.Factory<Int, CrunchyCollectionEntity>
}