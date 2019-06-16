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

package co.anitrend.support.crunchyroll.data.dao.query.rss

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssNews
import io.wax911.support.data.dao.ISupportQuery

@Dao
interface CrunchyRssNewsDao : ISupportQuery<CrunchyRssNews> {

    @Query("delete from CrunchyRssNews")
    suspend fun clearTable()


    @Query("select * from CrunchyRssNews order by date(publishedOn) desc")
    suspend fun findAll(): List<CrunchyRssNews>

    @Query("select * from CrunchyRssNews order by date(publishedOn) desc")
    fun findAllX(): LiveData<List<CrunchyRssNews>>

    @Query("select * from CrunchyRssNews order by date(publishedOn) desc")
    fun findByAllFactory(): DataSource.Factory<Int, CrunchyRssNews>
}