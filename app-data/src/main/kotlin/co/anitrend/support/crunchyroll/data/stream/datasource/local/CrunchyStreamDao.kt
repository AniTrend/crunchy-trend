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

package co.anitrend.support.crunchyroll.data.stream.datasource.local

import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.arch.database.dao.IDao
import co.anitrend.support.crunchyroll.data.stream.entity.CrunchyStreamEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CrunchyStreamDao : IDao<CrunchyStreamEntity> {

    @Query(
        """
        select count(mediaId) from CrunchyStreamEntity
    """
    )
    override suspend fun count(): Int

    @Query("""
        delete from CrunchyStreamEntity
        """)
    override suspend fun clearTable()

    @Query(
        """
        delete from CrunchyStreamEntity
        where mediaId = :mediaId
        """
    )
    suspend fun clearTableById(mediaId: Long)

    @Query(
        """
        select *
        from CrunchyStreamEntity 
        where mediaId = :mediaId
    """
    )
    fun findStreamByMediaIdFlow(
        mediaId: Long
    ): Flow<CrunchyStreamEntity?>
}