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

package co.anitrend.support.crunchyroll.data.stream.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.domain.stream.enums.CrunchyStreamQuality

@Entity(
    indices = [
        Index("mediaId", unique = true)
    ]
)
internal data class CrunchyStreamEntity(
    val mediaId: Long,
    val playHead: Int,
    val subtitleLanguage: String,
    val audioLanguage: String,
    val format: String,
    val quality: CrunchyStreamQuality,
    val expires: Long,
    @PrimaryKey
    val url: String
)