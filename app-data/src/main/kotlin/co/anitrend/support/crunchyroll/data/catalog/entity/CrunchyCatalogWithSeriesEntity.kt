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

package co.anitrend.support.crunchyroll.data.catalog.entity

import androidx.room.Embedded
import androidx.room.Relation
import co.anitrend.support.crunchyroll.data.arch.common.ICrunchyRelationEntity
import co.anitrend.support.crunchyroll.data.series.entity.CrunchySeriesEntity

class CrunchyCatalogWithSeriesEntity : ICrunchyRelationEntity<CrunchyCatalogEntity, CrunchySeriesEntity> {
    @Embedded
    override lateinit var entity: CrunchyCatalogEntity
    @Relation(parentColumn = "catalogId", entityColumn = "id")
    override var relations: List<CrunchySeriesEntity> = emptyList()
}