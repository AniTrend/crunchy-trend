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

package co.anitrend.support.crunchyroll.feature.player.plugin.contract

import co.anitrend.support.crunchyroll.feature.player.model.track.contract.IMediaTrack
import co.anitrend.support.crunchyroll.feature.player.plugin.MediaPluginImpl
import com.devbrackets.android.exomedia.ExoMedia
import com.devbrackets.android.exomedia.ExoMedia.RendererType.*
import com.devbrackets.android.exomedia.listener.*
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.devbrackets.android.playlistcore.api.MediaPlayerApi
import com.devbrackets.android.playlistcore.api.PlaylistItem
import com.devbrackets.android.playlistcore.listener.MediaStatusListener
import com.devbrackets.android.playlistcore.listener.PlaylistListener
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.util.EventLogger
import timber.log.Timber

abstract class MediaPlugin<T: PlaylistItem> : MediaPlayerApi<T>, PlaylistListener<T>,
    OnPreparedListener, OnCompletionListener, OnErrorListener, OnSeekCompletionListener,
    OnBufferUpdateListener {

    protected val moduleTag = MediaPluginImpl::class.java.simpleName

    protected var prepared: Boolean = false
    protected var bufferPercent: Int = 0

    private var statusListener: MediaStatusListener<T>? = null

    abstract val exoMediaVideoView: VideoView

    protected fun ExoMedia.RendererType.matches(mimeType: String?): Boolean {
        return when (this) {
            AUDIO -> mimeType?.startsWith(TYPE_AUDIO)
            VIDEO -> mimeType?.startsWith(TYPE_VIDEO)
            CLOSED_CAPTION -> mimeType?.startsWith(TYPE_TEXT)
            METADATA -> mimeType?.startsWith(TYPE_META)
        } ?: false
    }

    /**
     * Sets up the plugin
     */
    open fun onInitializing() {
        exoMediaVideoView.handleAudioFocus = false
        exoMediaVideoView.setAnalyticsListener(
            EventLogger(null)
        )
    }

    /**
     * Finds tracks of a specific [rendererType]
     */
    protected inline fun tracksOfRenderType(
        rendererType: ExoMedia.RendererType,
        onTrackMatch: (
            format: Format,
            index: Int,
            groupIndex: Int,
            selectedIndex: Int
        ) -> Unit
    ) {
        val trackGroupArray = exoMediaVideoView
            .availableTracks
            ?.get(rendererType)

        if (trackGroupArray == null || trackGroupArray.isEmpty) {
            Timber.tag(moduleTag).v(
                "Player has no track groups for $rendererType"
            )
            return
        }

        for (groupIndex in 0 until trackGroupArray.length) {
            val selectedIndex = exoMediaVideoView.getSelectedTrackIndex(
                rendererType,
                groupIndex
            )
            Timber.tag(moduleTag).d("""
                Current selected track for render: $rendererType -> 
                groupIndex: $groupIndex selectedIndex: $selectedIndex
                    """.trimIndent()
            )
            val trackGroup = trackGroupArray.get(groupIndex)
            for (index in 0 until trackGroup.length) {
                val format = trackGroup.getFormat(index)

                if (!rendererType.matches(format.sampleMimeType)) {
                    Timber.tag(moduleTag).d(
                        "Skipping track with mimeType of ${format.sampleMimeType}"
                    )
                    continue
                }

                onTrackMatch(format, index, groupIndex, selectedIndex)
            }
        }
    }

    /**
     * Use specific media track
     */
    fun useMediaTrack(mediaTrack: IMediaTrack) {
        exoMediaVideoView.setTrack(
            mediaTrack.renderType,
            mediaTrack.groupIndex.toInt(),
            mediaTrack.trackIndex.toInt()
        )
    }

    override fun setMediaStatusListener(listener: MediaStatusListener<T>) {
        statusListener = listener
    }

    override fun onCompletion() {
        statusListener?.onCompletion(this)
    }

    override fun onError(e: Exception?): Boolean {
        return statusListener?.onError(this) == true
    }

    override fun onPrepared() {
        prepared = true
        statusListener?.onPrepared(this)
    }

    override fun onSeekComplete() {
        statusListener?.onSeekComplete(this)
    }

    override fun onBufferingUpdate(percent: Int) {
        bufferPercent = percent
        statusListener?.onBufferingUpdate(this, percent)
    }

    companion object {
        private const val TYPE_VIDEO = "video"
        private const val TYPE_AUDIO = "audio"
        private const val TYPE_TEXT = "text"
        private const val TYPE_META = "meta"
    }
}