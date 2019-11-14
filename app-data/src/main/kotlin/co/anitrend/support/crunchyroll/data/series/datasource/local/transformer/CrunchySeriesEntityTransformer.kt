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

package co.anitrend.support.crunchyroll.data.series.datasource.local.transformer

import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.series.entity.CrunchySeriesEntity
import co.anitrend.support.crunchyroll.data.series.model.CrunchySeriesModel

object CrunchySeriesEntityTransformer : ISupportMapperHelper<CrunchySeriesModel, CrunchySeriesEntity> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchySeriesModel): CrunchySeriesEntity {
        return CrunchySeriesEntity(
            seriesId = source.series_id,
            url = source.url,
            name = source.name,
            mediaType = CrunchyMediaType.valueOf(
                source.media_type
            ),
            landscapeImage = source.landscape_image?.fwide_url,
            portraitImage = source.portrait_image?.full_url,
            description = source.description
        )
    }
}