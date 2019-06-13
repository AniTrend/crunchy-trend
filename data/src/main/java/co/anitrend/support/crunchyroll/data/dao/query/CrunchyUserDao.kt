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
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser
import io.wax911.support.data.dao.ISupportQuery

@Dao
abstract class CrunchyUserDao : ISupportQuery<CrunchyUser> {

    @Query("delete from CrunchyUser")
    abstract suspend fun clearTable()


    @Query("select * from CrunchyUser")
    abstract suspend fun findAll(): List<CrunchyUser>?

    @Query("select * from CrunchyUser")
    abstract fun findAllX(): LiveData<List<CrunchyUser>?>



    @Query("select * from CrunchyUser where user_id = :userId")
    abstract suspend fun findById(
        userId: Int
    ): CrunchyUser?

    @Query("select * from CrunchyUser where user_id = :userId")
    abstract fun findByIdX(
        userId: Int
    ): LiveData<CrunchyUser?>
}