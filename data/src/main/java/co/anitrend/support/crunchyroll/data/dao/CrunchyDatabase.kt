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
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyLogin
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.dao.migration.MIGRATION_1_2
import co.anitrend.support.crunchyroll.data.dao.migration.MIGRATION_2_4
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.dao.converter.CrunchyImageSetConverter
import co.anitrend.support.crunchyroll.data.dao.converter.CrunchyThumbnailConverter
import co.anitrend.support.crunchyroll.data.dao.converter.CrunchyUserConverter
import co.anitrend.support.crunchyroll.data.datasource.local.api.*
import co.anitrend.support.crunchyroll.data.datasource.local.rss.CrunchyRssMediaDao
import co.anitrend.support.crunchyroll.data.datasource.local.rss.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import co.anitrend.support.crunchyroll.data.model.core.CrunchyLocale
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssMedia
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssNews
import co.anitrend.support.crunchyroll.data.model.series.CrunchySeries

@Database(
    entities = [
        CrunchySession::class, CrunchySessionCore::class,
        CrunchyLogin::class,

        CrunchyLocale::class,

        CrunchySeries::class, CrunchyCollection::class,
        CrunchyMedia::class,

        CrunchyRssNews::class, CrunchyRssMedia::class
    ],
    version = BuildConfig.DATABASE_SCHEMA_VERSION
)
@TypeConverters(
    value = [
        CrunchyImageSetConverter::class,
        CrunchyThumbnailConverter::class,
        CrunchyUserConverter::class
    ]
)
abstract class CrunchyDatabase: RoomDatabase() {

    abstract fun crunchyLocaleDao(): CrunchyLocaleDao

    abstract fun crunchySessionCoreDao(): CrunchySessionCoreDao

    abstract fun crunchySeriesDao(): CrunchySeriesDao
    abstract fun crunchyCollectionDao(): CrunchyCollectionDao
    abstract fun crunchyMediaDao(): CrunchyMediaDao

    abstract fun crunchyLoginDao(): CrunchyLoginDao
    abstract fun crunchySessionDao(): CrunchySessionDao

    abstract fun crunchyRssNewsDao(): CrunchyRssNewsDao
    abstract fun crunchyRssMediaDao(): CrunchyRssMediaDao

    companion object {
        fun newInstance(context: Context): CrunchyDatabase {
            return Room.databaseBuilder(
                context,
                CrunchyDatabase::class.java,
                "crunchy-db"
            ).fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_4)
                .build()
        }
    }
}