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

package co.anitrend.support.crunchyroll.feature.player.service

import co.anitrend.support.crunchyroll.feature.player.component.MediaImageProvider
import co.anitrend.support.crunchyroll.feature.player.model.stream.MediaStreamItem
import co.anitrend.support.crunchyroll.feature.player.plugin.PlaylistManagerPluginImpl
import com.devbrackets.android.playlistcore.components.playlisthandler.DefaultPlaylistHandler
import com.devbrackets.android.playlistcore.components.playlisthandler.PlaylistHandler
import com.devbrackets.android.playlistcore.service.BasePlaylistService
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

/**
 * A simple service that extends [BasePlaylistService] in order to provide
 * the application specific information required.
 */
class MediaPlayerService : BasePlaylistService<MediaStreamItem, PlaylistManagerPluginImpl>(), KoinComponent {

    /**
     * Links the [BasePlaylistManager] that contains the information for playback
     * to this service.
     *
     * NOTE: this is only used for retrieving information, it isn't used to register notifications
     * for playlist changes, however as long as the change isn't breaking (e.g. cleared playlist)
     * then nothing additional needs to be performed
     */
    override val playlistManager by inject<PlaylistManagerPluginImpl>()

    override fun newPlaylistHandler(): PlaylistHandler<MediaStreamItem> {
        val imageProvider = MediaImageProvider(applicationContext) {
            playlistHandler.updateMediaControls()
        }

        return DefaultPlaylistHandler.Builder(
            applicationContext,
            javaClass,
            playlistManager,
            imageProvider
        ).build()

    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching {
            // Releases and clears all the MediaPlayersMediaImageProvider
            playlistManager.mediaPlayers.forEach {
                it.release()
            }

            playlistManager.mediaPlayers.clear()
        }.exceptionOrNull()?.also {
            Timber.tag(moduleTag).e(it)
        }
    }

    companion object {
        private val moduleTag = MediaPlayerService::class.java.simpleName
    }
}