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

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.anitrend.arch.data.dao.ISupportQuery
import co.anitrend.support.crunchyroll.data.arch.database.dao.ISourceDao
import co.anitrend.support.crunchyroll.data.catalog.entity.CrunchyCatalogEntity
import co.anitrend.support.crunchyroll.data.catalog.entity.CrunchyCatalogWithSeriesEntity
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter
import kotlinx.coroutines.flow.Flow

@Dao
interface CrunchyCatalogDao : ISupportQuery<CrunchyCatalogEntity>, ISourceDao {

    @Query("""
        select count(catalogId) from CrunchyCatalogEntity
    """)
    override suspend fun count(): Int

    @Query("""
        delete from CrunchyCatalogEntity
        """)
    override suspend fun clearTable()


    @Transaction
    @Query("""
        select *
        from CrunchyCatalogEntity
        where catalogFilter = :catalogFilter
        """)
    suspend fun findMatching(catalogFilter: CrunchySeriesCatalogFilter): CrunchyCatalogWithSeriesEntity

    @Transaction
    @Query("""
        select *
        from CrunchyCatalogEntity 
        where catalogFilter = :catalogFilter
        """)
    fun findMatchingX(catalogFilter: CrunchySeriesCatalogFilter): Flow<CrunchyCatalogWithSeriesEntity>
}