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

package co.anitrend.support.crunchyroll.data.dao.query

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser
import io.wax911.support.data.dao.ISupportQuery

@Dao
abstract class CrunchySessionDao : ISupportQuery<CrunchySession> {

    @Query("delete from CrunchySession")
    abstract suspend fun clearTable()


    @Query("select * from CrunchySession order by date(expires) desc limit 1")
    abstract suspend fun findLatest(): CrunchySession?

    @Query("select * from CrunchySession order by date(expires) desc limit 1")
    abstract fun findLatestX(): LiveData<CrunchySession?>


    @Transaction
    suspend fun clearAndInsert(attribute: CrunchySession) {
        clearTable()
        insert(attribute)
    }

    @Transaction
    suspend fun insertSessionAndUser(attribute: CrunchySession, crunchyUserDao: CrunchyUserDao) {
        crunchyUserDao.upsert(attribute.user)
        clearAndInsert(attribute)
    }
}