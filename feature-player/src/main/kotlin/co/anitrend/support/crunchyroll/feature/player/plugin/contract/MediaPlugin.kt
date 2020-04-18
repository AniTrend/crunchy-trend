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

import co.anitrend.support.crunchyroll.feature.player.model.MediaStreamItem
import com.devbrackets.android.exomedia.listener.*
import com.devbrackets.android.playlistcore.api.MediaPlayerApi
import com.devbrackets.android.playlistcore.listener.MediaStatusListener

abstract class MediaPlugin : MediaPlayerApi<MediaStreamItem>, OnPreparedListener,
    OnCompletionListener, OnErrorListener, OnSeekCompletionListener, OnBufferUpdateListener {

    protected var prepared: Boolean = false
    protected var bufferPercent: Int = 0

    protected var statusListener: MediaStatusListener<MediaStreamItem>? = null

    override fun setMediaStatusListener(listener: MediaStatusListener<MediaStreamItem>) {
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
}