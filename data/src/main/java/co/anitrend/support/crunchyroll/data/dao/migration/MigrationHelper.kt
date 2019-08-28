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
            execSQL("DROP TABLE `CrunchyRssMedia`")
            execSQL("CREATE TABLE IF NOT EXISTS `CrunchyRssMedia` (`title` TEXT NOT NULL, `description` TEXT, `mediaId` INTEGER NOT NULL, `premiumAvailableDate` TEXT NOT NULL, `freeAvailableDate` TEXT NOT NULL, `seriesTitle` TEXT NOT NULL, `episodeTitle` TEXT NOT NULL, `episodeNumber` TEXT, `duration` INTEGER, `publisher` TEXT, `thumbnail` TEXT, `copyright` TEXT NOT NULL, PRIMARY KEY(`mediaId`))")
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
            execSQL("DROP TABLE `CrunchyRssMedia`")
            execSQL("CREATE TABLE IF NOT EXISTS `CrunchyRssMedia` (`media_id` INTEGER NOT NULL, `collection_id` INTEGER NOT NULL, `collection_etp_guid` TEXT NOT NULL, `series_id` INTEGER NOT NULL, `series_name` TEXT NOT NULL, `series_etp_guid` TEXT NOT NULL, `media_type` TEXT NOT NULL, `episode_number` TEXT NOT NULL, `duration` INTEGER NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `bif_url` TEXT NOT NULL, `url` TEXT NOT NULL, `clip` INTEGER NOT NULL, `available` INTEGER NOT NULL, `premium_available` INTEGER NOT NULL, `free_available` INTEGER NOT NULL, `availability_notes` TEXT NOT NULL, `available_time` TEXT NOT NULL, `premium_available_time` TEXT NOT NULL, `free_available_time` TEXT NOT NULL, `created` TEXT NOT NULL, `playhead` INTEGER NOT NULL, `large` TEXT NOT NULL, `full_url` TEXT NOT NULL, `fwide_url` TEXT, PRIMARY KEY(`collection_id`))")

            execSQL("DROP TABLE `CrunchyRssNews`")
            execSQL("CREATE TABLE IF NOT EXISTS `CrunchyRssNews` (`title` TEXT NOT NULL, `author` TEXT NOT NULL, `description` TEXT NOT NULL, `encoded` TEXT NOT NULL, `publishedOn` TEXT NOT NULL, `referenceLink` TEXT NOT NULL, `guid` TEXT NOT NULL, `copyright` TEXT NOT NULL, `publishedTime` INTEGER NOT NULL, PRIMARY KEY(`guid`))")
        }
    }
}