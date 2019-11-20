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

package co.anitrend.support.crunchyroll.data.media.model

import co.anitrend.support.crunchyroll.data.arch.ISO8601Date
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyImageSet

data class CrunchyMediaModel(
    val media_id: Long,
    val etp_guid: String,
    val collection_id: Long,
    val collection_etp_guid: String,
    val collection_name: String?,
    val series_id: Long,
    val series_etp_guid: String,
    val episode_number: String,
    val name: String,
    val description: String,
    val screenshot_image: CrunchyImageSet?,
    val url: String,
    val available_time: ISO8601Date,
    val premium_available_time: ISO8601Date,
    val free_available_time: ISO8601Date,
    val availability_notes: String,
    val duration: Int,
    val series_name: String,
    val playhead: Int
)