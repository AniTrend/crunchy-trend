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

package co.anitrend.support.crunchyroll.data.locale.datasource.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.arch.database.dao.IDao
import co.anitrend.support.crunchyroll.data.locale.entity.CrunchyLocaleEntity

@Dao
internal interface CrunchyLocaleDao : IDao<CrunchyLocaleEntity> {

    @Query("""
        select count(localeId) from CrunchyLocaleEntity
    """)
    override suspend fun count(): Int

    @Query("""
        delete from CrunchyLocaleEntity
        """)
    override suspend fun clearTable()


    @Query("""
        select * from CrunchyLocaleEntity
        """)
    suspend fun getAll() : List<CrunchyLocaleEntity>

    @Query("""
        select * from CrunchyLocaleEntity
        """)
    fun getAllX() : LiveData<List<CrunchyLocaleEntity>>
}