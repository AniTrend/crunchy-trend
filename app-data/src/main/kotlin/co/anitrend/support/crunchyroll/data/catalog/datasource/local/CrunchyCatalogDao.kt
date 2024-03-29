/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.data.catalog.datasource.local

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.anitrend.support.crunchyroll.data.arch.database.dao.IDao
import co.anitrend.support.crunchyroll.data.catalog.entity.CrunchyCatalogEntity
import co.anitrend.support.crunchyroll.data.catalog.entity.CrunchyCatalogWithSeriesEntity
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CrunchyCatalogDao : IDao<CrunchyCatalogEntity> {

    @Query("""
        select count(catalogId) from CrunchyCatalogEntity
    """)
    override suspend fun count(): Int

    @Query("""
        delete from CrunchyCatalogEntity
        """)
    override suspend fun clearTable()

    @Query("""
        delete from CrunchyCatalogEntity
        where catalogFilter = :catalogFilter
        """)
    suspend fun clearTableMatching(catalogFilter: CrunchySeriesCatalogFilter)


    @Transaction
    @Query("""
        select *
        from CrunchyCatalogEntity
        where catalogFilter = :catalogFilter
        order by catalogFilter asc, seriesRank asc
        """)
    suspend fun findMatching(catalogFilter: CrunchySeriesCatalogFilter): List<CrunchyCatalogWithSeriesEntity>

    @Transaction
    @Query("""
        select *
        from CrunchyCatalogEntity 
        where catalogFilter = :catalogFilter
        order by catalogFilter asc, seriesRank asc
        """)
    fun findMatchingFlow(catalogFilter: CrunchySeriesCatalogFilter): Flow<List<CrunchyCatalogWithSeriesEntity>>

    @Transaction
    @Query("""
        select *
        from CrunchyCatalogEntity
        order by catalogFilter asc, seriesRank asc
        """)
    fun findAllFlow(): Flow<List<CrunchyCatalogWithSeriesEntity>>

    @Query("""
        select *
        from CrunchyCatalogEntity 
        where catalogFilter = :catalogFilter
        order by catalogFilter asc, seriesRank asc
        """)
    @Transaction
    fun findMatchingFactory(
        catalogFilter: CrunchySeriesCatalogFilter
    ): DataSource.Factory<Int, CrunchyCatalogWithSeriesEntity>
}