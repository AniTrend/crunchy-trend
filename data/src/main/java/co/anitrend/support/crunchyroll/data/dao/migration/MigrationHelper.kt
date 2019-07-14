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

package co.anitrend.support.crunchyroll.data.dao.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 by lazy {
    object : Migration(1, 2) {
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
            database.apply {
                execSQL("DROP TABLE `CrunchyRssMedia`")
                execSQL("CREATE TABLE IF NOT EXISTS `CrunchyRssMedia` (`title` TEXT NOT NULL, `description` TEXT, `mediaId` INTEGER NOT NULL, `premiumAvailableDate` TEXT NOT NULL, `freeAvailableDate` TEXT NOT NULL, `seriesTitle` TEXT NOT NULL, `episodeTitle` TEXT NOT NULL, `episodeNumber` TEXT, `duration` INTEGER, `publisher` TEXT, `thumbnail` TEXT, `copyright` TEXT NOT NULL, PRIMARY KEY(`mediaId`))")
            }
        }
    }
}