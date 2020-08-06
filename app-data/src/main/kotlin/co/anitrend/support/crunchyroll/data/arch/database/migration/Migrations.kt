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

package co.anitrend.support.crunchyroll.data.arch.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    /**
     * Should run the necessary migrations.
     *
     * This class cannot access any generated Dao in this method.
     *
     * This method is already called inside a transaction and that transaction might actually be a
     * composite transaction of all necessary `Migration`s.
     *
     * @param database The database instance
     */
    override fun migrate(database: SupportSQLiteDatabase) {
        with(database) {
            val tableName = "CacheLogEntity"
            execSQL("""
                CREATE TABLE IF NOT EXISTS `${tableName}` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `request` TEXT NOT NULL, 
                `cacheItemId` INTEGER NOT NULL, 
                `timestamp` INTEGER NOT NULL
                )
            """.trimIndent())
        }
    }
}

internal val MIGRATION_2_3 = object : Migration(2, 3) {
    /**
     * Should run the necessary migrations.
     *
     * This class cannot access any generated Dao in this method.
     *
     * This method is already called inside a transaction and that transaction might actually be a
     * composite transaction of all necessary `Migration`s.
     *
     * @param database The database instance
     */
    override fun migrate(database: SupportSQLiteDatabase) {
        with(database) {
            val tableName = "CrunchyStreamEntity"
            execSQL("""
                CREATE TABLE IF NOT EXISTS `${tableName}` (
                `mediaId` INTEGER NOT NULL, 
                `playHead` INTEGER NOT NULL, 
                `subtitleLanguage` TEXT NOT NULL, 
                `audioLanguage` TEXT NOT NULL, 
                `format` TEXT NOT NULL, 
                `quality` TEXT NOT NULL, 
                `expires` INTEGER NOT NULL, 
                `url` TEXT NOT NULL, 
                PRIMARY KEY(`url`)
                )
            """.trimIndent())
        }
    }
}

internal val migrations = arrayOf(
    MIGRATION_1_2, MIGRATION_2_3
)
