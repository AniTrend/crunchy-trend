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

package co.anitrend.support.crunchyroll.data.model.media.contract

import co.anitrend.support.crunchyroll.data.model.core.CrunchyImageSet

interface ICrunchyMedia {
    val collection_id: Long
    val description: String
    val duration: Int
    val episode_number: String
    val media_id: Long
    val name: String
    val playhead: Int
    val screenshot_image: CrunchyImageSet
    val series_id: Long
    val series_name: String
    val url: String

    val actualEpisodeNumber: Int
        get() {
            val regex = Regex("[^\\d.]")
            val filtered = episode_number.replace(regex, "")
            return filtered.toInt()
        }

    fun isShorterThanFiveMinutes() = duration < 300

    fun isFullEpisode() = actualEpisodeNumber % 1 == 0

    fun isSpecialEpisode() =
        episode_number == "SP" || episode_number.isBlank()

    fun qualifiesAsEpisode(): Boolean {
        return !isShorterThanFiveMinutes() ||
                isFullEpisode() ||
                !isSpecialEpisode()
    }
}