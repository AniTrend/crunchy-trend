/*
 *    Copyright 2019 AniTrend
 *
 *    Licensed under the Apache License, Version 2.0 (the """License""");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an """AS IS""" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package co.anitrend.support.crunchyroll.data.media.datasource.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.arch.data.dao.ISupportQuery
import co.anitrend.support.crunchyroll.data.media.entity.CrunchyMediaEntity

@Dao
interface CrunchyMediaDao : ISupportQuery<CrunchyMediaEntity> {

    @Query("""
        delete from CrunchyMediaEntity
        """)
    suspend fun clearTable()


    @Query("""
        select * 
        from CrunchyMediaEntity 
        where mediaId = :mediaId
        """)
     suspend fun findByMediaId(
        mediaId: String
    ): CrunchyMediaEntity?

    @Query("""
        select * 
        from CrunchyMediaEntity 
        where mediaId = :mediaId
        """)
    fun findByMediaIdX(
        mediaId: String
    ): LiveData<CrunchyMediaEntity?>



    @Query("""
        select * 
        from CrunchyMediaEntity 
        where collectionId = :collectionId 
        order by length(episodeNumber), episodeNumber
        """)
    suspend fun findByCollectionId(
        collectionId: Long
    ): List<CrunchyMediaEntity>

    @Query("""
        select * 
        from CrunchyMediaEntity 
        where collectionId = :collectionId 
        order by length(episodeNumber), episodeNumber
        """)
    fun findByCollectionIdX(
        collectionId: Long
    ): LiveData<List<CrunchyMediaEntity>>

    @Query("""
        select * 
        from CrunchyMediaEntity 
        where collectionId = :collectionId 
        order by length(episodeNumber), episodeNumber
        """)
    fun findByCollectionIdFactory(
        collectionId: Long
    ): DataSource.Factory<Int, CrunchyMediaEntity>
}