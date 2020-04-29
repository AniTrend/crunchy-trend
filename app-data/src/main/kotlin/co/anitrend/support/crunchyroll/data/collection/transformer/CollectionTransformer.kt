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

package co.anitrend.support.crunchyroll.data.collection.transformer

import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.data.collection.entity.CrunchyCollectionEntity
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection

internal object CollectionTransformer :
    ISupportMapperHelper<CrunchyCollectionEntity, CrunchyCollection> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyCollectionEntity): CrunchyCollection {
        return CrunchyCollection(
            collectionId = source.collectionId,
            seriesId = source.seriesId,
            name = source.name,
            description = source.description,
            mediaType = source.mediaType,
            season = source.season,
            complete = source.complete,
            landscapeImage = source.landscapeImage,
            portraitImage = source.portraitImage,
            availabilityNotes = source.availabilityNotes,
            created = source.created
        )
    }
}