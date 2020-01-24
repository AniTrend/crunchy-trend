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

package co.anitrend.support.crunchyroll.data.media.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [
        Index(value = ["seriesId"]),
        Index(value = ["collectionId"])
    ]
)
data class CrunchyMediaEntity(
    @PrimaryKey
    val mediaId: Long,
    val etpGuid: String,
    val collectionId: Long,
    val collectionEtpGuid: String?,
    val seriesId: Long,
    val seriesEtpGuid: String?,
    val episodeNumber: String,
    val name: String,
    val description: String,
    val screenshotImage: String?,
    val url: String,
    val availableTime: Long,
    val premiumAvailableTime: Long,
    val freeAvailableTime: Long,
    val availabilityNotes: String,
    val duration: Int,
    val seriesName: String,
    val playhead: Int,
    val isSpecialEpisode: Boolean
)