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

package co.anitrend.support.crunchyroll.data.session.datasource.local

import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.arch.database.dao.IDao
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionEntity

@Dao
internal interface CrunchySessionDao : IDao<CrunchySessionEntity> {

    @Query("""
        select count(sessionId) from CrunchySessionEntity
        """)
    override suspend fun count(): Int

    @Query("""
        delete from CrunchySessionEntity
        """)
    override suspend fun clearTable()


    @Query("""
        select * 
        from CrunchySessionEntity 
        where sessionId = :sessionId
        """)
    suspend fun findBySessionId(
        sessionId: String?
    ): CrunchySessionEntity?
}