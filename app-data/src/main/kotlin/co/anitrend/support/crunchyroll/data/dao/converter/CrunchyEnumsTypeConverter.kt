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
import co.anitrend.support.crunchyroll.domain.user.enums.CrunchyAccessType
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchyMediaType

class CrunchyEnumsTypeConverter {

    @TypeConverter fun fromAccessType(value: CrunchyAccessType?) = value?.ordinal
    @TypeConverter fun toAccessType(value: Int?) = value?.toEnum<CrunchyAccessType>()

    @TypeConverter fun fromMediaType(value: CrunchyMediaType?) = value?.ordinal
    @TypeConverter fun toMediaType(value: Int?) = value?.toEnum<CrunchyMediaType>()


    private inline fun <reified T : Enum<T>> Int.toEnum(): T = enumValues<T>()[this]
}