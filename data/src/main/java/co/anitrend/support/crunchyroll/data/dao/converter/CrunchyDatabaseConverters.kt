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

package co.anitrend.support.crunchyroll.data.dao.converter

import androidx.room.TypeConverter
import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.wax911.support.data.dao.RoomConverter

object CrunchyDatabaseConverters {

    private val gson by lazy(LazyThreadSafetyMode.NONE) {
        GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()
    }

    val CRUNCHY_USER_CONVERTER = object : RoomConverter<CrunchyUser> {

        /**
         * Convert database types back to the original type
         *
         * @see androidx.room.TypeConverter
         * @param dbValue saved database value type
         */
        @TypeConverter
        override fun fromDatabaseValue(dbValue: String): CrunchyUser? {
            val token = object : TypeToken<CrunchyUser?>(){}.type
            return gson.fromJson(dbValue, token)
        }

        /**
         * Convert custom types to database values that room can persist,
         * recommended persistence format is json strings.
         *
         * @see androidx.room.TypeConverter
         * @param entity item which room should convert
         */
        @TypeConverter
        override fun toDatabaseValue(entity: CrunchyUser?): String {
            return gson.toJson(entity)
        }
    }

    val CRUNCHY_IMAGE_SET_CONVERTER = object : RoomConverter<CrunchyImageSet> {

        /**
         * Convert database types back to the original type
         *
         * @see androidx.room.TypeConverter
         * @param dbValue saved database value type
         */
        @TypeConverter
        override fun fromDatabaseValue(dbValue: String): CrunchyImageSet? {
            val token = object : TypeToken<CrunchyImageSet?>(){}.type
            return gson.fromJson(dbValue, token)
        }

        /**
         * Convert custom types to database values that room can persist,
         * recommended persistence format is json strings.
         *
         * @see androidx.room.TypeConverter
         * @param entity item which room should convert
         */
        @TypeConverter
        override fun toDatabaseValue(entity: CrunchyImageSet?): String {
            return gson.toJson(entity)
        }
    }
}