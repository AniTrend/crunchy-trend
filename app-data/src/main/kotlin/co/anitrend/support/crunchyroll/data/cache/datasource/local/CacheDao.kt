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

package co.anitrend.support.crunchyroll.data.cache.datasource.local

import androidx.room.Dao
import androidx.room.Query
import co.anitrend.arch.data.dao.ISupportQuery
import co.anitrend.support.crunchyroll.data.arch.database.dao.ISourceDao
import co.anitrend.support.crunchyroll.data.cache.entity.CacheLogEntity
import co.anitrend.support.crunchyroll.data.cache.model.CacheRequest

@Dao
internal interface CacheDao : ISupportQuery<CacheLogEntity>, ISourceDao{
    @Query("""
        delete from CacheLogEntity
    """)
    override suspend fun clearTable()

    @Query("""
        select count(id) from CacheLogEntity
    """)
    override suspend fun count(): Int

    @Query("""
        select count(id)
        from CacheLogEntity 
        where request = :request and cacheItemId = :itemId
        """)
    suspend fun countMatching(request: CacheRequest, itemId: Long): Int


    @Query("""
        select * 
        from CacheLogEntity 
        where request = :request and cacheItemId = :itemId
        """)
    suspend fun getCacheLog(request: CacheRequest, itemId: Long): CacheLogEntity?
}