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
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import io.wax911.support.data.dao.ISupportQuery

@Dao
interface CrunchyMediaDao : ISupportQuery<CrunchyMedia> {

    @Query("delete from CrunchyMedia")
    suspend fun clearTable()


    @Query("select * from CrunchyMedia where media_id = :mediaId")
     suspend fun findByMediaId(
        mediaId: String
    ): CrunchyMedia?

    @Query("select * from CrunchyMedia where media_id = :mediaId")
    fun findByMediaIdX(
        mediaId: Long
    ): LiveData<CrunchyMedia?>



    @Query("select * from CrunchyMedia where collection_id = :collectionId order by media_id desc")
    suspend fun findByCollectionId(
        collectionId: Int
    ): List<CrunchyMedia>

    @Query("select * from CrunchyMedia where collection_id = :collectionId order by media_id desc")
    fun findByCollectionIdX(
        collectionId: Int
    ): LiveData<List<CrunchyMedia>>

    @Query("select * from CrunchyMedia where collection_id = :collectionId order by media_id desc")
    fun findByCollectionIdFactory(
        collectionId: Int
    ): DataSource.Factory<Int, CrunchyMedia>
}