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

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.collection.model.contract.ICrunchyCollection
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyImageSet

@Entity(
    indices = [
        Index(value = ["series_id"])
    ]
)
data class CrunchyCollection(
    override val availability_notes: String,
    override val media_type: CrunchyMediaType,
    override val series_id: Int,
    @PrimaryKey
    override val collection_id: Int,
    override val complete: Boolean,
    override val name: String,
    override val description: String,
    override val landscape_image: CrunchyImageSet?,
    override val portrait_image: CrunchyImageSet?,
    override val season: String,
    override val created: String
) : ICrunchyCollection