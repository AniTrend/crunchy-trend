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

package co.anitrend.support.crunchyroll.data.episode.datasource.local.transformer

import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.data.episode.entity.EpisodeFeedEntity
import co.anitrend.support.crunchyroll.data.episode.helper.EpisodeFeedHelper
import co.anitrend.support.crunchyroll.data.episode.model.CrunchyEpisodeModel
import co.anitrend.support.crunchyroll.data.util.extension.rcf822ToUnixTime
import java.util.*

internal object EpisodeFeedEntityTransformer : ISupportMapperHelper<CrunchyEpisodeModel, EpisodeFeedEntity> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyEpisodeModel): EpisodeFeedEntity {
        return EpisodeFeedEntity(
            mediaId = source.mediaId.toLong(),
            title = source.title,
            description = source.description,
            freeAvailableTime = source.freeAvailableDate.rcf822ToUnixTime(),
            premiumAvailableTime = source.premiumAvailableDate.rcf822ToUnixTime(),
            episodeThumbnail = EpisodeFeedHelper.highestQuality(
                source.thumbnail
            )?.url,
            episodeDuration = EpisodeFeedHelper.durationFormatted(
                source.duration
            ),
            episodeTitle = source.episodeTitle,
            episodeNumber = source.episodeNumber,
            seriesTitle = source.seriesTitle,
            copyright = source.copyright,
            isCountryWhiteListed = false,
            isPremiumEnabled = false,
            subtitles = Collections.emptyList()
        )
    }

    fun transform(source: CrunchyEpisodeModel, locale: Locale, hasPremiumAccess: Boolean): EpisodeFeedEntity {
        return transform(source).copy(
            subtitles = EpisodeFeedHelper.getSubtitles(
                source.subtitleLanguages,
                locale
            ),
            isCountryWhiteListed = EpisodeFeedHelper.isAllowed(
                source.restriction,
                locale
            ),
            isPremiumEnabled = hasPremiumAccess
        )
    }
}