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

package co.anitrend.support.crunchyroll.data.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.entity.CrunchyLoginEntity
import co.anitrend.support.crunchyroll.data.collection.datasource.local.CrunchyCollectionDao
import co.anitrend.support.crunchyroll.data.collection.model.CrunchyCollection
import co.anitrend.support.crunchyroll.data.dao.converter.CrunchyEnumsTypeCoverter
import co.anitrend.support.crunchyroll.data.dao.migrations.migrations
import co.anitrend.support.crunchyroll.data.episode.datasource.local.CrunchyRssEpisodeDao
import co.anitrend.support.crunchyroll.data.episode.entity.EpisodeFeedEntity
import co.anitrend.support.crunchyroll.data.locale.datasource.local.CrunchyLocaleDao
import co.anitrend.support.crunchyroll.data.locale.entity.CrunchyLocaleEntity
import co.anitrend.support.crunchyroll.data.media.datasource.local.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.media.model.CrunchyMedia
import co.anitrend.support.crunchyroll.data.news.datasource.local.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.news.entity.NewsEntity
import co.anitrend.support.crunchyroll.data.series.datasource.local.CrunchySeriesDao
import co.anitrend.support.crunchyroll.data.series.model.CrunchySeries
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionCoreEntity
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionEntity

@Database(
    entities = [
        CrunchySessionEntity::class, CrunchySessionCoreEntity::class,
        CrunchyLoginEntity::class,

        CrunchyLocaleEntity::class,

        CrunchySeries::class, CrunchyCollection::class,
        CrunchyMedia::class,

        NewsEntity::class, EpisodeFeedEntity::class
    ],
    version = BuildConfig.DATABASE_SCHEMA_VERSION
)
@TypeConverters(
    value = [CrunchyEnumsTypeCoverter::class]
)
abstract class CrunchyDatabase: RoomDatabase() {

    abstract fun crunchyLocaleDao(): CrunchyLocaleDao

    abstract fun crunchySessionCoreDao(): CrunchySessionCoreDao

    abstract fun crunchyCollectionDao(): CrunchyCollectionDao
    abstract fun crunchyMediaDao(): CrunchyMediaDao
    abstract fun crunchySeriesDao(): CrunchySeriesDao

    abstract fun crunchyLoginDao(): CrunchyLoginDao
    abstract fun crunchySessionDao(): CrunchySessionDao

    abstract fun crunchyRssNewsDao(): CrunchyRssNewsDao
    abstract fun crunchyRssMediaDao(): CrunchyRssEpisodeDao

    companion object {
        fun newInstance(context: Context): CrunchyDatabase {
            return Room.databaseBuilder(
                context,
                CrunchyDatabase::class.java,
                "crunchy-db"
            ).fallbackToDestructiveMigration()
                .addMigrations(*migrations)
                .build()
        }
    }
}