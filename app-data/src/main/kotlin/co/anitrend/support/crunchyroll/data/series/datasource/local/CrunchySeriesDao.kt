/*
 *    Copyright 2019 AniTrend
 *
 *    Licensed under the Apache License, Version 2.0 (the """License""");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an """AS IS""" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package co.anitrend.support.crunchyroll.data.series.datasource.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.anitrend.support.crunchyroll.data.arch.database.dao.IDao
import co.anitrend.support.crunchyroll.data.series.entity.CrunchySeriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CrunchySeriesDao : IDao<CrunchySeriesEntity> {

    @Query("""
        select count(id) from CrunchySeriesEntity
    """)
    override suspend fun count(): Int

    @Query("""
        delete from CrunchySeriesEntity
        """)
    override suspend fun clearTable()

    @Query("""
        delete from CrunchySeriesEntity
        where id = :seriesId
        """)
    suspend fun clearTableById(seriesId: Long)

    @Query("""
        delete from CrunchySeriesEntity
        where name match :seriesName 
        """)
    suspend fun clearTableByMatch(seriesName: String)

    @Query("""
        delete from CrunchySeriesEntity
        where name like :prefix
        """)
    suspend fun clearTableByPrefix(prefix: String)
    @Query("""
        delete from CrunchySeriesEntity
        where genres like :genre
        """)
    suspend fun clearTableByGenre(genre: String)


    @Query("""
        select *
        from CrunchySeriesEntity 
        where id = :seriesId
        """)
    suspend fun findBySeriesId(
        seriesId: Long
    ): CrunchySeriesEntity?

    @Query("""
        select *
        from CrunchySeriesEntity 
        where id = :seriesId
    """)
    fun findBySeriesIdX(
        seriesId: Long
    ): LiveData<CrunchySeriesEntity?>

    @Query("""
        select *
        from CrunchySeriesEntity 
        where id = :seriesId
    """)
    fun findBySeriesIdFlow(
        seriesId: Long
    ): Flow<CrunchySeriesEntity?>


    @Query("""
        select count(id)
        from CrunchySeriesEntity 
        where name like :seriesName 
    """)
    suspend fun countBySeriesName(seriesName: String): Int

    @Query("""
        select *
        from CrunchySeriesEntity 
        where name match :seriesName 
        order by name collate nocase asc
    """)
    suspend fun findBySeriesName(
        seriesName: String
    ): List<CrunchySeriesEntity>

    @Query("""
        select *
        from CrunchySeriesEntity 
        where name match :seriesName 
        order by name collate nocase asc
    """)
    fun findBySeriesNameX(
        seriesName: String
    ): LiveData<List<CrunchySeriesEntity>>

    @Transaction
    @Query("""
        select *
        from CrunchySeriesEntity as se
        join CrunchySeriesFtsEntity as sf on (se.id = sf.docid)
        where sf.name match :seriesName 
        order by se.name collate nocase asc
    """)
    fun findBySeriesNameFactory(
        seriesName: String
    ): DataSource.Factory<Int, CrunchySeriesEntity>

    @Query("""
        select *
        from CrunchySeriesEntity
        where name like :seriesName 
        order by name collate nocase asc
    """)
    fun findBySeriesNameWildCardFactory(
        seriesName: String
    ): DataSource.Factory<Int, CrunchySeriesEntity>



    @Query("""
        select *
        from CrunchySeriesEntity 
        order by name collate nocase asc
        """)
    suspend fun findAll(): List<CrunchySeriesEntity>

    @Query("""
        select *
        from CrunchySeriesEntity 
        order by name collate nocase asc
        """)
    fun findAllX(): LiveData<List<CrunchySeriesEntity>>

    @Query("""
        select *
        from CrunchySeriesEntity 
        order by name collate nocase asc
        """)
    fun findAllFactory(): DataSource.Factory<Int, CrunchySeriesEntity>

    @Query("""
        select count(id)
        from CrunchySeriesEntity
        where name like :prefix
        """)
    suspend fun countStartingWith(
        prefix: String
    ): Int

    @Query("""
        select *
        from CrunchySeriesEntity
        where name like :prefix
        order by name collate nocase asc
        """)
    fun findAllStartingWithFactory(
        prefix: String
    ): DataSource.Factory<Int, CrunchySeriesEntity>


    @Query("""
        select count(id)
        from CrunchySeriesEntity 
        where genres like :genre
        """)
    fun countContainingGenre(
        genre: String
    ): Int

    @Query("""
        select *
        from CrunchySeriesEntity 
        where genres like :genre
        order by name collate nocase asc
        """)
    fun findAllContainingGenreFactory(
        genre: String
    ): DataSource.Factory<Int, CrunchySeriesEntity>
}