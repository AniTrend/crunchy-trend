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

package co.anitrend.support.crunchyroll.data.collection.datasource.local.transformer

import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.data.collection.entity.CrunchyCollectionEntity
import co.anitrend.support.crunchyroll.data.collection.model.CrunchyCollectionModel
import co.anitrend.support.crunchyroll.data.util.extension.iso8601ToUnixTime
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchyMediaType

object CrunchyCollectionEntityTransformer :
    ISupportMapperHelper<CrunchyCollectionModel, CrunchyCollectionEntity> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyCollectionModel): CrunchyCollectionEntity {
        return CrunchyCollectionEntity(
            collectionId = source.collection_id.toLong(),
            etpGuid = source.etp_guid,
            seriesId = source.series_id.toLong(),
            seriesEtpGuid = source.series_etp_guid,
            name = source.name,
            description = source.description,
            mediaType = CrunchyMediaType.valueOf(
                source.media_type
            ),
            season = source.season,
            complete = source.complete,
            landscapeImage = source.landscape_image?.full_url,
            portraitImage = source.portrait_image?.full_url,
            availabilityNotes = source.availability_notes,
            created = source.created.iso8601ToUnixTime()
        )
    }
}