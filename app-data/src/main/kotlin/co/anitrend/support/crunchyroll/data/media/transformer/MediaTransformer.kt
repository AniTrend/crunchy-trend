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

package co.anitrend.support.crunchyroll.data.media.transformer

import co.anitrend.arch.data.transformer.ISupportTransformer
import co.anitrend.support.crunchyroll.data.media.entity.CrunchyMediaEntity
import co.anitrend.support.crunchyroll.domain.media.entities.CrunchyMedia

internal object MediaTransformer : ISupportTransformer<CrunchyMediaEntity, CrunchyMedia> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyMediaEntity): CrunchyMedia {
        return CrunchyMedia(
            mediaId = source.mediaId,
            collectionId = source.collectionId,
            seriesId = source.seriesId,
            episodeNumber = source.episodeNumber,
            name = source.name,
            description = source.description,
            screenshotImage = source.screenshotImage,
            url = source.url,
            availableTime = source.availableTime,
            premiumAvailableTime = source.premiumAvailableTime,
            freeAvailableTime = source.freeAvailableTime,
            availabilityNotes = source.availabilityNotes,
            duration = source.duration,
            seriesName = source.seriesName,
            isSpecialEpisode = source.isSpecialEpisode
        )
    }
}