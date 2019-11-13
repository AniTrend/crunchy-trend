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

package co.anitrend.support.crunchyroll.data.episode.helper

import co.anitrend.support.crunchyroll.data.episode.model.CrunchyEpisodeRestriction
import co.anitrend.support.crunchyroll.data.episode.model.EpisodeThumbnail
import java.util.*
import java.util.concurrent.TimeUnit

object EpisodeFeedHelper {

    fun highestQuality(thumbnails: List<EpisodeThumbnail>?) = thumbnails?.maxBy { it.width }

    fun durationFormatted(duration: Int?) : String {
        return duration?.let {
            val minutes = TimeUnit.SECONDS.toMinutes(it.toLong())
            val seconds = it - TimeUnit.MINUTES.toSeconds(minutes)
            String.format(Locale.getDefault(), if (seconds < 10) "%d:0%d" else "%d:%d", minutes, seconds)
        } ?: "00:00"
    }

    fun getSubtitles(subtitles: String?, locale: Locale): List<String> {
        if (subtitles != null) {
            // en - us,es - la,es - es
            return subtitles
                .split(',')
                .map {
                    val firstDelimiter = it.indexOf(' ')
                    val lastDelimiter = it.lastIndexOf(' ')
                    val language = it.substring(
                        IntRange(0, firstDelimiter - 1)
                    )
                    val country = it.substring(
                        IntRange(lastDelimiter + 1, it.length - 1)
                    ).toUpperCase(locale)

                    "$language$country"
                }
        }
        return Collections.emptyList()
    }

    fun isAllowed(restriction: CrunchyEpisodeRestriction?, locale: Locale): Boolean {
        if (restriction != null) {
            val country = locale.country

            // relationship="allow" type="country"
            // elements: ua ae gb us
            val restrictions = restriction.elements
                .split(' ')
                .map { it.toLowerCase(locale) }
            if (restriction.relationship == "allow" && restriction.type == "country")
                return restrictions.contains(country.toLowerCase(locale))
        }
        return true
    }
}