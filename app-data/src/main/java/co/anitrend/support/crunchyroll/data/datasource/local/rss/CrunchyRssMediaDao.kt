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

package co.anitrend.support.crunchyroll.data.datasource.local.rss

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.arch.data.dao.ISupportQuery
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssMedia

@Dao
interface CrunchyRssMediaDao : ISupportQuery<CrunchyRssMedia> {

    @Query("delete from CrunchyRssMedia")
    suspend fun clearTable()


    @Query("""
        select * from CrunchyRssMedia 
        order by premiumAvailableTime desc, 
        seriesTitle desc, episodeTitle desc, episodeNumber desc
        """)
    suspend fun findAll(): List<CrunchyRssMedia>

    @Query("""
        select * from CrunchyRssMedia 
        order by premiumAvailableTime desc, 
        seriesTitle desc, episodeTitle desc, episodeNumber desc
        """)
    fun findAllX(): LiveData<List<CrunchyRssMedia>>

    @Query("""
        select * from CrunchyRssMedia 
        order by premiumAvailableTime desc, 
        seriesTitle desc, episodeTitle desc, episodeNumber desc
        """)
    fun findByAllFactory(): DataSource.Factory<Int, CrunchyRssMedia>

    @Query("""
        select * from CrunchyRssMedia 
        where seriesTitle = :seriesSlug 
        order by premiumAvailableTime desc, 
        seriesTitle desc, episodeTitle desc, episodeNumber desc
        """)
    fun findBySlugFactory(
        seriesSlug: String?
    ): DataSource.Factory<Int, CrunchyRssMedia>
}