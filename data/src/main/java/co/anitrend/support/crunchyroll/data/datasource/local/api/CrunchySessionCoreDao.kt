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

package co.anitrend.support.crunchyroll.data.datasource.local.api

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.arch.data.dao.ISupportQuery

@Dao
interface CrunchySessionCoreDao : ISupportQuery<CrunchySessionCore> {

    @Query("delete from CrunchySessionCore")
    suspend fun clearTable()


    @Query("select * from CrunchySessionCore order by sessionCoreId desc limit 1")
    suspend fun findLatest(): CrunchySessionCore?

    @Query("select * from CrunchySessionCore order by sessionCoreId desc limit 1")
    fun findLatestX(): LiveData<CrunchySessionCore?>


    @Transaction
    suspend fun saveSession(attribute: CrunchySessionCore) {
        val lastSaved = findLatest()
        if (lastSaved == null)
            insert(attribute)
        else {
            val updated = attribute.copy(
                sessionCoreId = lastSaved.sessionCoreId
            )
            update(updated)
        }
    }
}