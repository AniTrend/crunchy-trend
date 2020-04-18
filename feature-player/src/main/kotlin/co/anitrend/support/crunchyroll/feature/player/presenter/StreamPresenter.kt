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

package co.anitrend.support.crunchyroll.feature.player.presenter

import android.content.Context
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.domain.stream.enums.CrunchyStreamQuality
import co.anitrend.support.crunchyroll.feature.player.model.MediaStreamItem
import kotlin.math.round

class StreamPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    fun getOptimalStreamIndex(model: List<MediaStreamItem>): Int {
        return model.indexOfFirst {
            it.mediaStreamQuality == CrunchyStreamQuality.adaptive
        }
    }

    fun calculateBitRate(bitrate: Int): Float {
        val megabit = (bitrate / 1024f) / 1024f
        return megabit.round(1)
    }

    private fun Float.round(decimals: Int): Float {
        var multiplier = 1.0f
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }

    companion object {
        const val separator = "\u2022"
    }
}