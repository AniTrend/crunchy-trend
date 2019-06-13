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
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.dao.converter.CrunchyDatabaseConverters
import co.anitrend.support.crunchyroll.data.dao.query.*
import co.anitrend.support.crunchyroll.data.dao.view.collection.CollectionWithMedia
import co.anitrend.support.crunchyroll.data.dao.view.series.SeriesWithCollection
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import co.anitrend.support.crunchyroll.data.model.series.CrunchySeries
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser

@Database(
    entities = [
        CrunchySession::class, CrunchySessionCore::class,
        CrunchyUser::class, CrunchySeries::class,
        CrunchyCollection::class, CrunchyLoginDao::class
    ],
    views = [
        CollectionWithMedia::class,
        SeriesWithCollection::class
    ],
    version = BuildConfig.DATABASE_SCHEMA_VERSION
)
@TypeConverters(CrunchyDatabaseConverters::class)
abstract class CrunchyDatabase: RoomDatabase() {

    abstract fun crunchySessionCoreDao(): CrunchySessionCoreDao

    abstract fun crunchySeriesDao(): CrunchySeriesDao
    abstract fun crunchyCollectionDao(): CrunchyCollectionDao

    abstract fun crunchyUserDao(): CrunchyUserDao
    abstract fun crunchyLoginDao(): CrunchyLoginDao
    abstract fun crunchySessionDao(): CrunchySessionDao

    companion object {
        fun newInstance(context: Context): CrunchyDatabase {
            return Room.databaseBuilder(
                context,
                CrunchyDatabase::class.java,
                "crunchy-db"
            ).fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}