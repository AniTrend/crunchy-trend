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

package co.anitrend.support.crunchyroll.data.arch.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.anitrend.support.crunchyroll.data.arch.database.common.ICrunchyDatabase
import co.anitrend.support.crunchyroll.data.arch.database.converters.CrunchyEnumsTypeConverter
import co.anitrend.support.crunchyroll.data.arch.database.converters.CrunchyTypeConverters
import co.anitrend.support.crunchyroll.data.arch.database.migration.migrations
import co.anitrend.support.crunchyroll.data.authentication.entity.CrunchyLoginEntity
import co.anitrend.support.crunchyroll.data.cache.entity.CacheLogEntity
import co.anitrend.support.crunchyroll.data.catalog.entity.CrunchyCatalogEntity
import co.anitrend.support.crunchyroll.data.collection.entity.CrunchyCollectionEntity
import co.anitrend.support.crunchyroll.data.episode.entity.EpisodeFeedEntity
import co.anitrend.support.crunchyroll.data.locale.entity.CrunchyLocaleEntity
import co.anitrend.support.crunchyroll.data.media.entity.CrunchyMediaEntity
import co.anitrend.support.crunchyroll.data.news.entity.NewsEntity
import co.anitrend.support.crunchyroll.data.series.entity.CrunchySeriesEntity
import co.anitrend.support.crunchyroll.data.series.entity.CrunchySeriesFtsEntity
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionCoreEntity
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionEntity
import co.anitrend.support.crunchyroll.data.stream.entity.CrunchyStreamEntity

@Database(
    entities = [
        CrunchySessionEntity::class,
        CrunchySessionCoreEntity::class,
        CrunchyLoginEntity::class,
        CrunchyLocaleEntity::class,
        CrunchySeriesFtsEntity::class,
        CrunchySeriesEntity::class,
        CrunchyCollectionEntity::class,
        CrunchyMediaEntity::class,
        CrunchyCatalogEntity::class,
        NewsEntity::class,
        EpisodeFeedEntity::class,
        CacheLogEntity::class,
        CrunchyStreamEntity::class
    ],
    version = CrunchyDatabase.DATABASE_SCHEMA_VERSION
)
@TypeConverters(
    value = [
        CrunchyEnumsTypeConverter::class,
        CrunchyTypeConverters::class
    ]
)
internal abstract class CrunchyDatabase: RoomDatabase(), ICrunchyDatabase {

    companion object {
        const val DATABASE_SCHEMA_VERSION = 3
        fun newInstance(context: Context): CrunchyDatabase {
            return Room.databaseBuilder(
                context,
                CrunchyDatabase::class.java,
                "crunchy-db"
            ).addMigrations(*migrations)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}