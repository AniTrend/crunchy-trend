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

package co.anitrend.support.crunchyroll.data.transformer

import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssMedia
import co.anitrend.support.crunchyroll.data.model.rss.MediaThumbnail
import co.anitrend.support.crunchyroll.domain.entities.result.rss.MediaListing
import java.util.*
import java.util.concurrent.TimeUnit

object MediaListingTransformer: ISupportMapperHelper<CrunchyRssMedia, MediaListing> {

    private fun List<MediaThumbnail>?.highestQuality() = this?.maxBy { it.width }

    private fun durationFormatted(duration: Int?) : String {
        return duration?.let {
            val minutes = TimeUnit.SECONDS.toMinutes(it.toLong())
            val seconds = it - TimeUnit.MINUTES.toSeconds(minutes)
            String.format(Locale.getDefault(), if (seconds < 10) "%d:0%d" else "%d:%d", minutes, seconds)
        } ?: "00:00"
    }

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyRssMedia): MediaListing {
        return MediaListing(
            id = source.mediaId,
            title = source.title,
            description = source.description,
            freeAvailableTime = source.freeAvailableTime,
            premiumAvailableTime = source.premiumAvailableTime,
            episodeThumbnail = source.thumbnail.highestQuality()?.url,
            episodeDuration = durationFormatted(source.duration),
            episodeTitle = source.episodeTitle,
            episodeNumber = source.episodeNumber,
            copyright = source.copyright
        )
    }
}