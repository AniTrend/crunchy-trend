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
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyLogin
import io.wax911.support.data.dao.ISupportQuery

@Dao
interface CrunchyLoginDao : ISupportQuery<CrunchyLogin> {

    @Query("delete from CrunchyLogin")
    suspend fun clearTable()


    @Query("select * from CrunchyLogin order by date(expires) desc limit 1")
    suspend fun findLatest(): CrunchyLogin?

    @Query("select * from CrunchyLogin order by date(expires) desc limit 1")
    fun findLatestX(): LiveData<CrunchyLogin?>


    @Transaction
    suspend fun clearAndInsert(attribute: CrunchyLogin) {
        clearTable()
        insert(attribute)
    }
}