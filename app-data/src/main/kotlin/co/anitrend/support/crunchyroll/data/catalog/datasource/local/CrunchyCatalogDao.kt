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
import co.anitrend.arch.data.dao.ISupportQuery
import co.anitrend.support.crunchyroll.data.catalog.entity.CrunchyCatalogEntity
import co.anitrend.support.crunchyroll.data.catalog.entity.CrunchyCatalogWithSeriesEntity
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter
import kotlinx.coroutines.flow.Flow

@Dao
interface CrunchyCatalogDao : ISupportQuery<CrunchyCatalogEntity> {

    @Query("""
        delete from CrunchyCatalogEntity
        """)
    suspend fun clearTable()


    @Query("""
        select *
        from CrunchyCatalogEntity
        where catalogFilter = :catalogFilter
        """)
    suspend fun findAll(catalogFilter: CrunchySeriesCatalogFilter): List<CrunchyCatalogWithSeriesEntity>

    @Query("""
        select *
        from CrunchyCatalogEntity 
        where catalogFilter = :catalogFilter
        """)
    fun findAllX(catalogFilter: CrunchySeriesCatalogFilter): Flow<List<CrunchyCatalogWithSeriesEntity>>

    @Query("""
        select *
        from CrunchyCatalogEntity 
        where catalogFilter = :catalogFilter
        """)
    fun findAllFactory(catalogFilter: CrunchySeriesCatalogFilter): DataSource.Factory<Int, CrunchyCatalogWithSeriesEntity>
}