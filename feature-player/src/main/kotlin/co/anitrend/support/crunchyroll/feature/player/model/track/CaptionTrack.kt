/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.feature.player.model.track

import co.anitrend.support.crunchyroll.feature.player.model.track.contract.IMediaTrack
import com.devbrackets.android.exomedia.ExoMedia
import com.google.android.exoplayer2.Format

data class CaptionTrack(
    override val title: CharSequence,
    override val trackIndex: Short,
    override val groupIndex: Short,
    override val renderType: ExoMedia.RendererType
) : IMediaTrack {
    override var selected = false

    /**
     * Returns a string representation of the object.
     */
    override fun toString() = title.toString()

    companion object {
        fun map(format: Format, index: Short, groupIndex: Short): CaptionTrack {
            val title = format.label ?: format.language ?: "${groupIndex}:$index"
            return CaptionTrack(
                title = title,
                trackIndex = index,
                groupIndex = groupIndex,
                renderType = ExoMedia.RendererType.CLOSED_CAPTION
            )
        }
    }
}