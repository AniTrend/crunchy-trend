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

package co.anitrend.support.crunchyroll.data.news.datasource.local

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.arch.database.dao.IDao
import co.anitrend.support.crunchyroll.data.news.entity.NewsEntity

@Dao
internal interface CrunchyRssNewsDao : IDao<NewsEntity> {

    @Query("""
        select count(guid) from NewsEntity
    """)
    override suspend fun count(): Int

    @Query("""
        delete from NewsEntity
        """)
    override suspend fun clearTable()


    @Query("""
        select * 
        from NewsEntity 
        order by publishedOn desc
        """)
    fun findAllCursor(): Cursor


    @Query("""
        select * 
        from NewsEntity 
        order by publishedOn desc
        limit :perPage offset :page
        """)
    fun findCursor(page: Int, perPage: Int): Cursor


    @Query("""
        select * 
        from NewsEntity 
        where guid = :guid
        """)
    fun findByIdCursor(guid: String): Cursor


    @Query("""
        select * 
        from NewsEntity 
        order by publishedOn desc
        """)
    suspend fun findAll(): List<NewsEntity>

    @Query("""
        select * 
        from NewsEntity 
        order by publishedOn desc
        """)
    fun findAllX(): LiveData<List<NewsEntity>>

    @Query("""
        select * 
        from NewsEntity 
        order by publishedOn desc
        """)
    fun findByAllFactory(): DataSource.Factory<Int, NewsEntity>
}