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

package co.anitrend.support.crunchyroll.data.media.datasource.local.transformer

import co.anitrend.arch.data.transformer.ISupportTransformer
import co.anitrend.support.crunchyroll.data.media.entity.CrunchyMediaEntity
import co.anitrend.support.crunchyroll.data.media.helper.CrunchyMediaHelper
import co.anitrend.support.crunchyroll.data.media.model.CrunchyMediaModel
import co.anitrend.support.crunchyroll.data.util.extension.iso8601ToUnixTime

internal object CrunchyMediaEntityTransformer : ISupportTransformer<CrunchyMediaModel, CrunchyMediaEntity> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyMediaModel): CrunchyMediaEntity {
        return CrunchyMediaEntity(
            mediaId = source.media_id,
            etpGuid = source.etp_guid,
            collectionId = source.collection_id,
            collectionEtpGuid = source.collection_etp_guid,
            seriesId = source.series_id,
            seriesEtpGuid = source.series_etp_guid,
            episodeNumber = CrunchyMediaHelper.getActualEpisodeNumber(source),
            name = source.name,
            description = source.description,
            screenshotImage = source.screenshot_image?.full_url,
            url = source.url,
            availableTime = source.available_time.iso8601ToUnixTime(),
            premiumAvailableTime = source.premium_available_time.iso8601ToUnixTime(),
            freeAvailableTime = source.free_available_time.iso8601ToUnixTime(),
            availabilityNotes = source.availability_notes,
            duration = source.duration,
            seriesName = source.series_name,
            playhead = source.playhead,
            isSpecialEpisode = !CrunchyMediaHelper.qualifiesAsEpisode(source)
        )
    }
}