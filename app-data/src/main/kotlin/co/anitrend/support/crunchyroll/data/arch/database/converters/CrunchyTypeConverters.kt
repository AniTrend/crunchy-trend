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

package co.anitrend.support.crunchyroll.data.arch.database.converters

import androidx.room.TypeConverter
import co.anitrend.support.crunchyroll.data.arch.database.extension.fromCommaSeparatedValues
import co.anitrend.support.crunchyroll.data.arch.database.extension.toCommaSeparatedValues
import org.threeten.bp.Instant

internal class CrunchyTypeConverters {

    @TypeConverter fun fromList(value: List<String>) = value.toCommaSeparatedValues()
    @TypeConverter fun toList(value: String) = value.fromCommaSeparatedValues()

    @TypeConverter fun toInstant(value: Long?) = value?.let { Instant.ofEpochMilli(it) }
    @TypeConverter fun fromInstant(date: Instant?) = date?.toEpochMilli()
}