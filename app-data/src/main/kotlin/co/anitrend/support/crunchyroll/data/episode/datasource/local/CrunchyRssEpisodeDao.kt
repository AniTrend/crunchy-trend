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

package co.anitrend.support.crunchyroll.data.episode.datasource.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.arch.database.dao.IDao
import co.anitrend.support.crunchyroll.data.episode.entity.EpisodeFeedEntity

@Dao
internal interface CrunchyRssEpisodeDao : IDao<EpisodeFeedEntity> {

    @Query("""
        select count(mediaId) from EpisodeFeedEntity 
    """)
    override suspend fun count(): Int

    @Query("""
        delete from EpisodeFeedEntity
        """)
    override suspend fun clearTable()

    @Query(
        """
        select * from EpisodeFeedEntity 
        order by premiumAvailableTime desc, seriesTitle desc, 
        length(episodeNumber), episodeNumber
        """
    )
    suspend fun findAll(): List<EpisodeFeedEntity>

    @Query(
        """
        select * from EpisodeFeedEntity 
        order by premiumAvailableTime desc, seriesTitle desc, 
        length(episodeNumber), episodeNumber
        """
    )
    fun findAllX(): LiveData<List<EpisodeFeedEntity>>

    @Query(
        """
        select * from EpisodeFeedEntity 
        order by premiumAvailableTime desc, seriesTitle desc, 
        length(episodeNumber), episodeNumber
        """
    )
    fun findByAllFactory(): DataSource.Factory<Int, EpisodeFeedEntity>

    @Query(
        """
        select * from EpisodeFeedEntity 
        where seriesTitle = :seriesSlug
        order by premiumAvailableTime desc, seriesTitle desc, 
        length(episodeNumber), episodeNumber
        """
    )
    fun findBySlugFactory(
        seriesSlug: String?
    ): DataSource.Factory<Int, EpisodeFeedEntity>
}