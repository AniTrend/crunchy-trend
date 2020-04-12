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
import co.anitrend.arch.data.dao.ISupportQuery
import co.anitrend.support.crunchyroll.data.arch.database.dao.ISourceDao
import co.anitrend.support.crunchyroll.data.series.entity.CrunchySeriesEntity

@Dao
interface CrunchySeriesDao : ISupportQuery<CrunchySeriesEntity>, ISourceDao {

    @Query("""
        select count(id) from CrunchySeriesEntity
    """)
    override suspend fun count(): Int

    @Query("""
        delete from CrunchySeriesEntity
        """)
    override suspend fun clearTable()


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
        where name match :seriesName 
        order by name asc
    """)
    suspend fun findBySeriesName(
        seriesName: String
    ): List<CrunchySeriesEntity>

    @Query("""
        select *
        from CrunchySeriesEntity 
        where name match :seriesName 
        order by name asc
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
        order by se.name asc
    """)
    fun findBySeriesNameFactory(
        seriesName: String
    ): DataSource.Factory<Int, CrunchySeriesEntity>



    @Query("""
        select *
        from CrunchySeriesEntity 
        order by name asc
        """)
    suspend fun findAll(): List<CrunchySeriesEntity>

    @Query("""
        select *
        from CrunchySeriesEntity 
        order by name asc
        """)
    fun findAllX(): LiveData<List<CrunchySeriesEntity>>

    @Query("""
        select *
        from CrunchySeriesEntity 
        order by name asc
        """)
    fun findAllFactory(): DataSource.Factory<Int, CrunchySeriesEntity>

    @Query("""
        select *
        from CrunchySeriesEntity
        where name like :prefix
        order by name asc
        """)
    fun findAllStartingWithFactory(
        prefix: String
    ): DataSource.Factory<Int, CrunchySeriesEntity>

    @Query("""
        select *
        from CrunchySeriesEntity 
        where genres in(:option)
        order by name asc
        """)
    fun findAllContainingGenre(
        option: String
    ): DataSource.Factory<Int, CrunchySeriesEntity>
}