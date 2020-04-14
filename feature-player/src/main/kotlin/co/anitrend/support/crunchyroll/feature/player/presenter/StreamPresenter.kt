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
import android.net.Uri
import android.webkit.WebView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.LifecycleOwner
import co.anitrend.arch.extension.attachComponent
import co.anitrend.arch.extension.getCompatDrawable
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.domain.stream.enums.CrunchyStreamQuality
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.component.SourceFactoryProvider
import coil.Coil
import coil.api.load
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener
import com.devbrackets.android.exomedia.ui.widget.VideoControls
import com.devbrackets.android.exomedia.ui.widget.VideoControlsCore
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import timber.log.Timber
import java.io.IOException

class StreamPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    private val moduleTag = StreamPresenter::class.java.simpleName

    private val loadErrorHandlingPolicy =
        object : LoadErrorHandlingPolicy {
        override fun getRetryDelayMsFor(
            dataType: Int,
            loadDurationMs: Long,
            exception: IOException?,
            errorCount: Int
        ): Long = 1500

        override fun getMinimumLoadableRetryCount(dataType: Int): Int = 3

        override fun getBlacklistDurationMsFor(
            dataType: Int,
            loadDurationMs: Long,
            exception: IOException?,
            errorCount: Int
        ): Long = 500
    }

    fun getOptimalStream(model: List<MediaStream>?): MediaStream? {
        return model?.find {
            it.quality == CrunchyStreamQuality.adaptive
        }
    }

    private fun getStreamUri(mediaStream: MediaStream): Uri {
        return Uri.parse(mediaStream.url)
    }

    fun setupMediaSource(
        sourceFactoryProvider: SourceFactoryProvider,
        mediaStream: MediaStream,
        player: VideoView,
        thumbnail: String?,
        lifecycleOwner: LifecycleOwner
    ) {
        val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
        val userAgent = WebView(context).settings.userAgentString

        Coil.load(context, thumbnail) {
            target { player.setPreviewImage(it) }
            lifecycle(lifecycleOwner)
        }

        val streamUri = getStreamUri(mediaStream)

        val source = HlsMediaSource.Factory(
            sourceFactoryProvider.provide(userAgent, bandwidthMeter)
        )
            .setLoadErrorHandlingPolicy(loadErrorHandlingPolicy)
            .setAllowChunklessPreparation(true)
            .createMediaSource(streamUri)

        player.setVideoURI(streamUri, source)
    }

    fun customizeControls(
        showOptions: () -> Unit,
        videoView: VideoView,
        videoControlsCore: VideoControlsCore?,
        payload: NavigationTargets.MediaPlayer.Payload?,
        controlsVisibilityListener : VideoControlsVisibilityListener
    ) {
        val options = AppCompatImageButton(
            videoView.context
        ).apply {
            setBackgroundResource(android.R.color.transparent)
            setImageResource(R.drawable.ic_more_vert_white_24dp)
            setOnClickListener { showOptions() }
        }

        val videoControls = videoControlsCore as? VideoControls
        if (videoControls != null) {
            videoControls.setVisibilityListener(controlsVisibilityListener)

            videoControls.addExtraView(options)
            videoControls.setFastForwardButtonEnabled(true)
            videoControls.setFastForwardDrawable(
                context.getCompatDrawable(R.drawable.ic_forward_30_white_48dp)
            )

            videoControls.setRewindButtonEnabled(true)
            videoControls.setRewindDrawable(
                context.getCompatDrawable(R.drawable.ic_replay_10_white_48dp)
            )

            videoControls.setPlayPauseDrawables(
                context.getCompatDrawable(R.drawable.ic_play_circle_outline_white_56dp),
                context.getCompatDrawable(R.drawable.ic_pause_circle_outline_white_56dp)
            )

            if (payload?.collectionName.isNullOrBlank()) {
                videoControls.setTitle(payload?.episodeTitle)
            } else {
                videoControls.setTitle(payload?.collectionName)
                videoControls.setSubTitle(payload?.episodeTitle)
            }
        }
    }
}