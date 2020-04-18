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

import android.app.Application
import co.anitrend.arch.extension.lifecycle.SupportLifecycle
import co.anitrend.support.crunchyroll.feature.player.model.MediaStreamItem
import co.anitrend.support.crunchyroll.feature.player.service.MediaPlayerService
import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener
import com.devbrackets.android.exomedia.ui.widget.VideoControls
import com.devbrackets.android.playlistcore.manager.ListPlaylistManager

/**
 * A PlaylistManager that extends the [ListPlaylistManager] for use with the
 * [MediaPlayerService] which extends [com.devbrackets.android.playlistcore.service.BasePlaylistService].
 */
class PlaylistManagerPlugin(
    context: Application
) : ListPlaylistManager<MediaStreamItem>(context, MediaPlayerService::class.java),
    SupportLifecycle {

    override val moduleTag = PlaylistManagerPlugin::class.java.simpleName

    /**
     * Triggered when the lifecycleOwner reaches it's onResume state
     *
     * @see [androidx.lifecycle.LifecycleOwner]
     */
    override fun onResume() {
        super.onResume()
        invokePausePlay()
    }

    /**
     * Triggered when the lifecycleOwner reaches it's onPause state
     *
     * @see [androidx.lifecycle.LifecycleOwner]
     */
    override fun onPause() {
        super.onPause()
        // find a way to avoid playing an item on pause that is already paused
        invokePausePlay()
    }

    /**
     * Triggered when the lifecycleOwner reaches it's onDestroy state
     *
     * @see [androidx.lifecycle.LifecycleOwner]
     */
    override fun onDestroy() {
        super.onDestroy()
        invokeStop()
    }

    /**
     * Note: You can call [.getMediaPlayers] and add it manually in the activity,
     * however we have this helper method to allow registration of the media controls
     * repeatListener provided by ExoMedia's [com.devbrackets.android.exomedia.ui.widget.VideoControls]
     */
    fun addMediaPlugin(mediaPlugin: MediaPluginImpl) {
        mediaPlayers.add(mediaPlugin)
        updateVideoControls(mediaPlugin)
        registerPlaylistListener(mediaPlugin)
    }

    /**
     * Note: You can call [.getMediaPlayers] and remove it manually in the activity,
     * however we have this helper method to remove the registered repeatListener from [.addVideoApi]
     */
    fun removeMediaPlugin(mediaPlugin: MediaPluginImpl) {
        (mediaPlugin.exoMediaVideoView.videoControls as? VideoControls)?.setButtonListener(null)

        unRegisterPlaylistListener(mediaPlugin)
        mediaPlayers.remove(mediaPlugin)
    }

    /**
     * Updates the available controls on the VideoView and links the
     * button events to the playlist service and this.
     *
     * @param videoApi The VideoApi to link
     */
    private fun updateVideoControls(mediaPlugin: MediaPluginImpl) {
        (mediaPlugin.exoMediaVideoView.videoControls as? VideoControls)?.let {
            it.setPreviousButtonRemoved(false)
            it.setNextButtonRemoved(false)
            it.setButtonListener(ControlsListener())
        }
    }

    /**
     * An implementation of the [VideoControlsButtonListener] that provides
     * integration with the playlist service.
     */
    private inner class ControlsListener : VideoControlsButtonListener {
        override fun onPlayPauseClicked(): Boolean {
            invokePausePlay()
            return true
        }

        override fun onPreviousClicked(): Boolean {
            invokePrevious()
            return false
        }

        override fun onNextClicked(): Boolean {
            invokeNext()
            return false
        }

        override fun onRewindClicked(): Boolean {
            return false
        }

        override fun onFastForwardClicked(): Boolean {
            return false
        }
    }
}