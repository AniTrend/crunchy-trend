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

package co.anitrend.support.crunchyroll.data.collection.model

import co.anitrend.support.crunchyroll.data.arch.ISO8601Date
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyImageSet

data class CrunchyCollection(
    val collection_id: Int,
    val etp_guid: String,
    val series_id: Int,
    val series_etp_guid: String,
    val name: String,
    val description: String,
    val media_type: String,
    val season: String,
    val complete: Boolean,
    val landscape_image: CrunchyImageSet?,
    val portrait_image: CrunchyImageSet?,
    val availability_notes: String?,
    val created: ISO8601Date
)