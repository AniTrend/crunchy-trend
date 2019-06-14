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

package co.anitrend.support.crunchyroll.data.model.series

import androidx.room.*
import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet
import co.anitrend.support.crunchyroll.data.model.series.contract.ICrunchySeries

@Entity
@Fts4
data class CrunchySeries(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowId: Int,
    override val media_type: CrunchyMediaType,
    override val series_id: Int,
    override val name: String,
    override val description: String,
    override val url: String,
    override val landscape_image: CrunchyImageSet?,
    override val portrait_image: CrunchyImageSet?
) : ICrunchySeries