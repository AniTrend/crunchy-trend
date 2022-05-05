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
import co.anitrend.support.crunchyroll.data.arch.database.dao.IDao
import co.anitrend.support.crunchyroll.data.collection.entity.CrunchyCollectionEntity

@Dao
internal interface CrunchyCollectionDao : IDao<CrunchyCollectionEntity> {

    @Query("""
        select count(collectionId) from CrunchyCollectionEntity
    """)
    override suspend fun count(): Int

    @Query("""
        delete from CrunchyCollectionEntity
        """)
    override suspend fun clearTable()

    @Query("""
        delete from CrunchyCollectionEntity
        where seriesId = :seriesId
        """)
    suspend fun clearTableById(seriesId: Long)


    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where collectionId = :collectionId
        """)
    fun findByCollectionIdX(
        collectionId: Long
    ): LiveData<CrunchyCollectionEntity?>

    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where collectionId = :collectionId
        """)
    fun findByCollectionId(
        collectionId: Long
    ): CrunchyCollectionEntity?



    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where seriesId = :seriesId
        order by length(season), season
        """)
    suspend fun findBySeriesId(
        seriesId: Long
    ): List<CrunchyCollectionEntity>

    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where seriesId = :seriesId
        order by length(season), season
        """)
    fun findBySeriesIdX(
        seriesId: Long
    ): LiveData<List<CrunchyCollectionEntity>>

    @Query("""
        select count(collectionId)
        from CrunchyCollectionEntity 
        where seriesId = :seriesId
        """)
    suspend fun countBySeriesId(
        seriesId: Long
    ): Int

    @Query("""
        select * 
        from CrunchyCollectionEntity 
        where seriesId = :seriesId
        order by length(season), season
        """)
    fun findBySeriesIdFactory(
        seriesId: Long
    ): DataSource.Factory<Int, CrunchyCollectionEntity>
}