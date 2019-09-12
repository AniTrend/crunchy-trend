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

package co.anitrend.support.crunchyroll.stream.presenter

import android.content.Context
import android.net.Uri
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.data.arch.StreamQualityContract
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import co.anitrend.support.crunchyroll.domain.entities.result.media.MediaStream
import coil.api.load
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.Util
import java.io.IOException

class StreamPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

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
        if (model?.isEmpty() != true) {
            model?.find { stream ->
                stream.quality == StreamQualityContract.LOW ||
                        stream.quality == StreamQualityContract.MEDIUM
            }
            return model?.filterNot { stream ->
                stream.quality == StreamQualityContract.LOW
            }?.first()
        }
        return null
    }
    
    private fun getStreamUri(mediaStream: MediaStream): Uri {
        return Uri.parse(mediaStream.url)
    }

    fun setupMediaSource(mediaStream: MediaStream, player: VideoView, thumbnail: String?) {
        val sourceFactory = DefaultDataSourceFactory(
            context, Util.getUserAgent(context, context.getString(R.string.app_name))
        )
        val streamUri = getStreamUri(mediaStream)

        val source = HlsMediaSource.Factory(sourceFactory)
            .setLoadErrorHandlingPolicy(loadErrorHandlingPolicy)
            .setAllowChunklessPreparation(true)
            .createMediaSource(streamUri)

        with (player) {
            /*coil.Coil.load(context, thumbnail) {
                target { setPreviewImage(it) }
            }*/
            setPreviewImage(Uri.parse(thumbnail))
            setVideoURI(streamUri, source)
        }
    }
}