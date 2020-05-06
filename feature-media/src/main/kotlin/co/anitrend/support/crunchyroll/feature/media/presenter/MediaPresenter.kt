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

package co.anitrend.support.crunchyroll.feature.media.presenter

import android.content.Context
import co.anitrend.support.crunchyroll.core.extensions.separator
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.domain.media.entities.CrunchyMedia
import java.util.*
import java.util.concurrent.TimeUnit

class MediaPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    companion object {
        fun CrunchyMedia.mediaDisplayName(): String {
            return "$episodeNumber $separator $name"
        }

        fun durationFormatted(duration: Int?) : String {
            return duration?.let {
                val minutes = TimeUnit.SECONDS.toMinutes(it.toLong())
                val seconds = it - TimeUnit.MINUTES.toSeconds(minutes)
                String.format(Locale.getDefault(), if (seconds < 10) "%d:0%d" else "%d:%d", minutes, seconds)
            } ?: "00:00"
        }
    }
}