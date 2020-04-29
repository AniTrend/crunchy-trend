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
import co.anitrend.support.crunchyroll.data.arch.database.extension.fromEnum
import co.anitrend.support.crunchyroll.data.arch.database.extension.toEnum
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchyMediaType
import co.anitrend.support.crunchyroll.domain.user.enums.CrunchyAccessType

internal class CrunchyEnumsTypeConverter {

    @TypeConverter fun fromAccessType(value: CrunchyAccessType?) = value?.fromEnum()
    @TypeConverter fun toAccessType(value: String?) = value?.toEnum<CrunchyAccessType>()

    @TypeConverter fun fromMediaType(value: CrunchyMediaType?) = value?.fromEnum()
    @TypeConverter fun toMediaType(value: String?) = value?.toEnum<CrunchyMediaType>()

    @TypeConverter fun fromCatalogFilter(value: CrunchySeriesCatalogFilter?) = value?.fromEnum()
    @TypeConverter fun toCatalogFilter(value: String?) = value?.toEnum<CrunchySeriesCatalogFilter>()
}