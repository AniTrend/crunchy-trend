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

package co.anitrend.support.crunchyroll.data.model.media

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet
import co.anitrend.support.crunchyroll.data.model.media.contract.ICrunchyMedia

@Entity(
    indices = [
        Index(value = ["series_id"]),
        Index(value = ["collection_id"])
    ]
)
data class CrunchyMedia(
    override val media_id: Long,
    @PrimaryKey
    override val collection_id: Long,
    val collection_etp_guid: String,
    override val series_id: Long,
    override val series_name: String,
    val series_etp_guid: String,
    val media_type: CrunchyMediaType,
    override val episode_number: String,
    override val duration: Int,
    override val name: String,
    override val description: String,
    @Embedded
    override val screenshot_image: CrunchyImageSet,
    val bif_url: String,
    override val url: String,
    val clip: Boolean,
    val available: Boolean,
    val premium_available: Boolean,
    val free_available: Boolean,
    val availability_notes: String,
    val available_time: String,
    val premium_available_time: String,
    val free_available_time: String,
    val created: String,
    override val playhead: Int
) : ICrunchyMedia