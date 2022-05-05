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

package co.anitrend.support.crunchyroll.data.media.helper

import androidx.annotation.VisibleForTesting
import androidx.core.text.isDigitsOnly
import co.anitrend.support.crunchyroll.data.media.model.CrunchyMediaModel

internal object CrunchyMediaHelper {
    fun getActualEpisodeNumber(model: CrunchyMediaModel): String {
        val regex = Regex("[^\\d.]")
        if (model.episode_number.isBlank())
            return "0"
        return model.episode_number.replace(regex, "")
    }

    @VisibleForTesting
    fun isShorterThanFiveMinutes(model: CrunchyMediaModel) =
        model.duration < 300

    @VisibleForTesting
    fun isFullEpisode(model: CrunchyMediaModel): Boolean {
        val episodeNumber = getActualEpisodeNumber(model)
        if (episodeNumber.isDigitsOnly())
            return episodeNumber.toInt() % 1 == 0
        return false
    }

    @VisibleForTesting
    fun isSpecialEpisode(model: CrunchyMediaModel) =
        model.episode_number == "SP" || model.episode_number.isBlank()

    fun qualifiesAsEpisode(model: CrunchyMediaModel): Boolean {
        return !isShorterThanFiveMinutes(model) ||
                isFullEpisode(model) ||
                !isSpecialEpisode(model)
    }
}