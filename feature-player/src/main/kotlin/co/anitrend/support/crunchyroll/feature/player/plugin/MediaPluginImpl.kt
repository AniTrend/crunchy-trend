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

package co.anitrend.support.crunchyroll.feature.player.plugin

import android.net.Uri
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import co.anitrend.support.crunchyroll.feature.player.component.SourceFactoryProvider
import co.anitrend.support.crunchyroll.feature.player.model.stream.MediaStreamItem
import co.anitrend.support.crunchyroll.feature.player.model.track.AudioTrack
import co.anitrend.support.crunchyroll.feature.player.model.track.CaptionTrack
import co.anitrend.support.crunchyroll.feature.player.model.track.VideoTrack
import co.anitrend.support.crunchyroll.feature.player.plugin.contract.MediaPlugin
import coil.Coil
import coil.api.load
import coil.request.RequestDisposable
import com.devbrackets.android.exomedia.ExoMedia
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener
import com.devbrackets.android.exomedia.ui.widget.VideoControls
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.devbrackets.android.playlistcore.data.PlaybackState
import com.devbrackets.android.playlistcore.manager.BasePlaylistManager
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.source.BaseMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.util.ArrayList

class MediaPluginImpl(
    private val coroutineScope: CoroutineScope,
    override val exoMediaVideoView: VideoView,
    userAgent: String,
    bandwidthMeter: DefaultBandwidthMeter,
    sourceFactoryProvider: SourceFactoryProvider
) : MediaPlugin<MediaStreamItem>() {

    private val mediaSourceFactory =
        sourceFactoryProvider.provide(userAgent, bandwidthMeter)
    private var disposable: RequestDisposable? = null

    override val isPlaying: Boolean
        get() = exoMediaVideoView.isPlaying

    override val handlesOwnAudioFocus: Boolean
        get() = false

    override val currentPosition: Long
        get() = if (prepared) exoMediaVideoView.currentPosition else 0

    override val duration: Long
        get() = if (prepared) exoMediaVideoView.duration else 0

    override val bufferedPercent: Int
        get() = bufferPercent

    init {
        exoMediaVideoView.setOnErrorListener(this)
        exoMediaVideoView.setOnPreparedListener(this)
        exoMediaVideoView.setOnCompletionListener(this)
        exoMediaVideoView.setOnSeekCompletionListener(this)
        exoMediaVideoView.setOnBufferUpdateListener(this)
    }

    /**
     * Retrieves a list of video tracks that can be used
     *
     * @return a list of media tracks
     * @see VideoTrack
     */
    fun availableVideoTracks(): List<VideoTrack>? {
        val videoTracks = ArrayList<VideoTrack>(5)
        tracksOfRenderType(
            ExoMedia.RendererType.VIDEO
        ) { format: Format, index: Int, groupIndex: Int, selectedIndex: Int ->
            val mediaTrack = VideoTrack.map(
                format,
                index.toShort(),
                groupIndex.toShort()
            )
            mediaTrack.selected = index == selectedIndex

            if (!videoTracks.contains(mediaTrack))
                videoTracks.add(mediaTrack)
            else
                Timber.tag(moduleTag).d(
                    "Video track already exists in selectable tracks: $mediaTrack"
                )
        }
        return videoTracks.apply { sortByDescending { it.bitrate } }
    }

    /**
     * Retrieves a list of audio tracks that can be used
     *
     * @return a list of media tracks
     * @see AudioTrack
     */
    fun availableAudioTracks(): List<AudioTrack>? {
        val audioTracks = ArrayList<AudioTrack>(2)
        tracksOfRenderType(
            ExoMedia.RendererType.AUDIO
        ) { format: Format, index: Int, groupIndex: Int, selectedIndex: Int ->
            val mediaTrack = AudioTrack.map(
                format,
                index.toShort(),
                groupIndex.toShort()
            )
            mediaTrack.selected = index == selectedIndex

            if (!audioTracks.contains(mediaTrack))
                audioTracks.add(mediaTrack)
            else
                Timber.tag(moduleTag).d(
                    "Audio track already exists in selectable tracks: $mediaTrack"
                )
        }
        return audioTracks
    }

    /**
     * Retrieves a list of video tracks that can be used
     *
     * @return a list of media tracks
     * @see CaptionTrack
     */
    fun availableCaptionTracks(): List<CaptionTrack>? {
        val captionTracks = ArrayList<CaptionTrack>(2)
        tracksOfRenderType(
            ExoMedia.RendererType.CLOSED_CAPTION
        ) { format: Format, index: Int, groupIndex: Int, selectedIndex: Int ->
            val mediaTrack = CaptionTrack.map(
                format,
                index.toShort(),
                groupIndex.toShort()
            )
            mediaTrack.selected = index == selectedIndex

            if (!captionTracks.contains(mediaTrack))
                captionTracks.add(mediaTrack)
            else
                Timber.tag(moduleTag).d(
                    "Caption track already exists in selectable tracks: $mediaTrack"
                )
        }
        return captionTracks
    }

    private fun checkIfOtherRequestIsOngoing() {
        if (disposable?.isDisposed != true) {
            val previous = disposable
            disposable = null
            previous?.dispose()
        }
    }

    private fun createHlsMediaSource(mediaUrl: String?): BaseMediaSource {
        val loadErrorHandlingPolicy = object : LoadErrorHandlingPolicy {
            override fun getRetryDelayMsFor(
                dataType: Int,
                loadDurationMs: Long,
                exception: IOException?,
                errorCount: Int
            ): Long {
                exception?.also {
                    Timber.tag(moduleTag).d(it)
                }
                return 1500
            }

            override fun getMinimumLoadableRetryCount(dataType: Int): Int = 3

            override fun getBlacklistDurationMsFor(
                dataType: Int,
                loadDurationMs: Long,
                exception: IOException?,
                errorCount: Int
            ): Long {
                exception?.also {
                    Timber.tag(moduleTag).d(it)
                }
                return 500
            }
        }
        // DefaultTrackSelector(AdaptiveTrackSelection(DefaultBandwidthMeter))
        return HlsMediaSource.Factory(mediaSourceFactory)
            .setLoadErrorHandlingPolicy(loadErrorHandlingPolicy)
            .setAllowChunklessPreparation(true)
            .createMediaSource(Uri.parse(mediaUrl))
    }

    private fun startPlaybackUsing(mediaStreamItem: MediaStreamItem) {
        checkIfOtherRequestIsOngoing()
        disposable = Coil.load(exoMediaVideoView.context, mediaStreamItem.thumbnailUrl) {
            target { exoMediaVideoView.setPreviewImage(it) }
        }
        if (!mediaStreamItem.downloaded)
            exoMediaVideoView.setVideoURI(
                Uri.parse(mediaStreamItem.mediaUrl),
                createHlsMediaSource(mediaStreamItem.mediaUrl)
            )
        else
            exoMediaVideoView.setVideoURI(
                Uri.parse(mediaStreamItem.downloadedMediaUri)
            )
    }

    override fun onPrepared() {
        super.onPrepared()
        coroutineScope.launch {
            val tracks = availableVideoTracks()
            val minimumQuality = tracks?.first {
                it.height == 480.toShort()
            }
            if (minimumQuality != null)
                useMediaTrack(minimumQuality)
        }
    }

    override fun play() {
        exoMediaVideoView.start()
    }

    override fun pause() {
        exoMediaVideoView.pause()
    }

    override fun stop() {
        exoMediaVideoView.stopPlayback()
    }

    override fun reset() {
        // Purposefully left blank
    }

    override fun release() {
        if (disposable?.isDisposed != true)
            disposable?.dispose()
        disposable = null
        exoMediaVideoView.suspend()
    }

    override fun setVolume(
        @FloatRange(from = 0.0, to = 1.0) left: Float,
        @FloatRange(from = 0.0, to = 1.0) right: Float
    ) {
        exoMediaVideoView.volume = (left + right) / 2
    }

    override fun seekTo(@IntRange(from = 0L) milliseconds: Long) {
        exoMediaVideoView.seekTo(milliseconds.toInt().toLong())
    }

    override fun handlesItem(item: MediaStreamItem): Boolean {
        return item.mediaType == BasePlaylistManager.VIDEO
    }

    override fun playItem(item: MediaStreamItem) {
        prepared = false
        bufferPercent = 0
        startPlaybackUsing(item)
    }

    /*
     * PlaylistListener methods used for keeping the VideoControls provided
     * by the ExoMedia VideoView up-to-date with the current playback state
     */
    override fun onPlaylistItemChanged(
        currentItem: MediaStreamItem?,
        hasNext: Boolean,
        hasPrevious: Boolean
    ): Boolean {
        (exoMediaVideoView.videoControls as? VideoControls)?.let { controls ->
            // Updates the VideoControls display text
            controls.setTitle(currentItem?.mediaTitle ?: "")
            controls.setSubTitle(currentItem?.mediaSubTitle ?: "")
            controls.setDescription(currentItem?.artist ?: "")

            // Updates the VideoControls button visibilities
            controls.setPreviousButtonEnabled(hasPrevious)
            controls.setNextButtonEnabled(hasNext)
        }

        return false
    }

    override fun onPlaybackStateChanged(playbackState: PlaybackState): Boolean {
        return false
    }

    fun setVisibilityListener(controlsVisibilityListener: VideoControlsVisibilityListener?) {
        val videoControls = exoMediaVideoView.videoControls as? VideoControls
        videoControls?.visibilityListener = controlsVisibilityListener
    }
}