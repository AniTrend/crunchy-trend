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

import co.anitrend.support.crunchyroll.feature.player.extension.SEPARATOR
import co.anitrend.support.crunchyroll.feature.player.extension.toMbps
import co.anitrend.support.crunchyroll.feature.player.model.track.contract.IMediaTrack
import co.anitrend.support.crunchyroll.feature.player.presenter.StreamPresenter
import com.devbrackets.android.exomedia.ExoMedia
import com.google.android.exoplayer2.Format

/**
 * Data holder for selectable tracks
 */
data class VideoTrack(
    val bitrate: Int,
    val width: Short,
    val height: Short,
    override val title: CharSequence,
    override val trackIndex: Short,
    override val groupIndex: Short,
    override val renderType: ExoMedia.RendererType
) : IMediaTrack {
    override var selected: Boolean = false

    /**
     * Returns a string representation of the object.
     */
    override fun toString() = title.toString()

    /**
     * Indicates whether some other object is "equal to" this one. Implementations must fulfil the following
     * requirements:
     *
     * * Reflexive: for any non-null value `x`, `x.equals(x)` should return true.
     * * Symmetric: for any non-null values `x` and `y`, `x.equals(y)` should return true if and only if `y.equals(x)` returns true.
     * * Transitive:  for any non-null values `x`, `y`, and `z`, if `x.equals(y)` returns true and `y.equals(z)` returns true, then `x.equals(z)` should return true.
     * * Consistent:  for any non-null values `x` and `y`, multiple invocations of `x.equals(y)` consistently return true or consistently return false, provided no information used in `equals` comparisons on the objects is modified.
     * * Never equal to null: for any non-null value `x`, `x.equals(null)` should return false.
     *
     * Read more about [equality](https://kotlinlang.org/docs/reference/equality.html) in Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is VideoTrack -> other.bitrate == bitrate &&
                    other.width == width &&
                    other.height == height
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = bitrate
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + title.hashCode()
        result = 31 * result + trackIndex
        result = 31 * result + groupIndex
        result = 31 * result + renderType.hashCode()
        result = 31 * result + selected.hashCode()
        return result
    }

    companion object {

        fun map(format: Format, index: Short, groupIndex: Short): VideoTrack {
            val title = """
                ${format.height}p $SEPARATOR ${format.bitrate.toMbps()}Mbps
                """.trimMargin()

            return VideoTrack(
                bitrate = format.bitrate,
                width = format.width.toShort(),
                height = format.height.toShort(),
                title = title,
                trackIndex = index,
                groupIndex = groupIndex,
                renderType = ExoMedia.RendererType.VIDEO
            )
        }
    }
}