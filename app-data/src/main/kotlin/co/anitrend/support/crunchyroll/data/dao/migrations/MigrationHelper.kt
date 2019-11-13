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

package co.anitrend.support.crunchyroll.data.dao.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
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
            execSQL("DROP TABLE `CrunchyRssEpisode`")
            execSQL("CREATE TABLE IF NOT EXISTS `CrunchyRssEpisode` (`title` TEXT NOT NULL, `description` TEXT, `mediaId` INTEGER NOT NULL, `premiumAvailableDate` TEXT NOT NULL, `freeAvailableDate` TEXT NOT NULL, `seriesTitle` TEXT NOT NULL, `episodeTitle` TEXT NOT NULL, `episodeNumber` TEXT, `duration` INTEGER, `publisher` TEXT, `thumbnail` TEXT, `copyright` TEXT NOT NULL, PRIMARY KEY(`mediaId`))")
        }
    }
}

val MIGRATION_2_4 = object : Migration(2, 4) {
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
            execSQL("DROP TABLE `CrunchyRssEpisode`")
            execSQL("CREATE TABLE IF NOT EXISTS `CrunchyRssEpisode` (`title` TEXT NOT NULL, `description` TEXT, `mediaId` INTEGER NOT NULL, `premiumAvailableDate` TEXT NOT NULL, `freeAvailableDate` TEXT NOT NULL, `seriesTitle` TEXT NOT NULL, `episodeTitle` TEXT NOT NULL, `episodeNumber` TEXT, `duration` INTEGER, `publisher` TEXT, `thumbnail` TEXT, `copyright` TEXT NOT NULL, `freeAvailableTime` INTEGER NOT NULL, `premiumAvailableTime` INTEGER NOT NULL, PRIMARY KEY(`mediaId`))")

            execSQL("DROP TABLE `CrunchyRssNews`")
            execSQL("CREATE TABLE IF NOT EXISTS `CrunchyRssNews` (`title` TEXT NOT NULL, `author` TEXT NOT NULL, `description` TEXT NOT NULL, `encoded` TEXT NOT NULL, `publishedOn` TEXT NOT NULL, `referenceLink` TEXT NOT NULL, `guid` TEXT NOT NULL, `copyright` TEXT NOT NULL, `publishedTime` INTEGER NOT NULL, PRIMARY KEY(`guid`))")
        }
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
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
            execSQL("DROP TABLE `CrunchyRssEpisode`")
            execSQL("CREATE TABLE IF NOT EXISTS `CrunchyRssEpisode` (`title` TEXT NOT NULL, `description` TEXT, `mediaId` INTEGER NOT NULL, `premiumAvailableDate` TEXT NOT NULL, `freeAvailableDate` TEXT NOT NULL, `seriesTitle` TEXT NOT NULL, `episodeTitle` TEXT NOT NULL, `episodeNumber` TEXT, `duration` INTEGER, `publisher` TEXT, `subtitleLanguages` TEXT, `restriction` TEXT, `thumbnail` TEXT, `copyright` TEXT NOT NULL, `freeAvailableTime` INTEGER NOT NULL, `premiumAvailableTime` INTEGER NOT NULL, PRIMARY KEY(`mediaId`))")

        }
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
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
            execSQL("DELETE FROM `CrunchySessionCore`")

            execSQL("DROP TABLE `CrunchyLogin`")
            execSQL("CREATE TABLE IF NOT EXISTS `CrunchyLogin` (`loginUserId` INTEGER NOT NULL, `auth` TEXT NOT NULL, `expires` TEXT NOT NULL, `user_id` INTEGER NOT NULL, `username` TEXT NOT NULL, `email` TEXT NOT NULL, `first_name` TEXT, `last_name` TEXT, `premium` TEXT, `access_type` TEXT, PRIMARY KEY(`loginUserId`))")

        }
    }
}