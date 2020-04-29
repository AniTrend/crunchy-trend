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

package co.anitrend.support.crunchyroll.data.episode.transformer

import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.data.episode.entity.EpisodeFeedEntity
import co.anitrend.support.crunchyroll.domain.episode.entities.CrunchyEpisodeFeed

internal object EpisodeFeedTransformer: ISupportMapperHelper<EpisodeFeedEntity, CrunchyEpisodeFeed> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: EpisodeFeedEntity): CrunchyEpisodeFeed {
        return CrunchyEpisodeFeed(
            id = source.mediaId,
            title = source.title,
            description = source.description,
            freeAvailableTime = source.freeAvailableTime,
            premiumAvailableTime = source.premiumAvailableTime,
            episodeThumbnail = source.episodeThumbnail,
            episodeDuration = source.episodeDuration,
            episodeTitle = source.episodeTitle,
            episodeNumber = source.episodeNumber,
            copyright = source.copyright,
            subtitles = source.subtitles,
            isCountryWhiteListed = source.isCountryWhiteListed,
            isPremiumEnabled = source.isPremiumEnabled
        )
    }
}