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

package co.anitrend.support.crunchyroll.data.series.model

import androidx.room.Entity
import androidx.room.Fts4
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyImageSet

@Entity
@Fts4
data class CrunchySeriesModel(
    val series_id: Long,
    val url: String,
    val name: String,
    val media_type: String,
    val landscape_image: CrunchyImageSet?,
    val portrait_image: CrunchyImageSet?,
    val description: String,
    val in_queue: Boolean,
    val rating: Int,
    val media_count: Int,
    val collection_count: Int,
    val publisher_name: String,
    val year: Int,
    val genres: List<String>
)