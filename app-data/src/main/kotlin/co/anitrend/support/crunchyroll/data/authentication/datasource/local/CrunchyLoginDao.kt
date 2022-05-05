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

package co.anitrend.support.crunchyroll.data.authentication.datasource.local

import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.arch.database.dao.IDao
import co.anitrend.support.crunchyroll.data.authentication.entity.CrunchyLoginEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CrunchyLoginDao : IDao<CrunchyLoginEntity> {

    @Query("""
        select count(userId) from CrunchyLoginEntity 
    """)
    override suspend fun count(): Int

    @Query("""
        delete from CrunchyLoginEntity
        """)
    override suspend fun clearTable()

    @Query("""
        select * 
        from CrunchyLoginEntity 
        where username = :account
        """)
    fun findLatestByAccountX(
        account: String
    ): Flow<CrunchyLoginEntity?>


    @Query("""
        select * 
        from CrunchyLoginEntity 
        where userId = :userId
        """)
    fun findByUserId(
        userId: Long
    ): CrunchyLoginEntity?

    @Query("""
        select * 
        from CrunchyLoginEntity 
        where userId = :userId
        """)
    fun findByUserIdX(
        userId: Long
    ): Flow<CrunchyLoginEntity?>
}