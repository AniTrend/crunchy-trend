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

import android.app.Application
import android.app.Service
import co.anitrend.arch.extension.lifecycle.SupportLifecycle
import com.devbrackets.android.playlistcore.api.PlaylistItem
import com.devbrackets.android.playlistcore.manager.ListPlaylistManager
import kotlin.reflect.KClass

abstract class PlaylistManagerPlugin<T: PlaylistItem>(
    application: Application,
    mediaServiceClass: KClass<out Service>
) : ListPlaylistManager<T>(application, mediaServiceClass.java), SupportLifecycle {

    override val moduleTag = PlaylistManagerPlugin::class.java.simpleName

    /**
     * Note: You can call [mediaPlayers] and add it manually in the activity,
     * however we have this helper method to allow registration of the media controls
     * repeatListener provided by ExoMedia's
     * [com.devbrackets.android.exomedia.ui.widget.VideoControls]
     */
    abstract fun addMediaPlugin(mediaPlugin: MediaPlugin<T>)

    /**
     * **N.B.:** You can call [mediaPlayers] and remove it manually in the activity,
     * however we have this helper method to remove the registered repeatListener from [addMediaPlugin]
     */
    abstract fun removeMediaPlugin(mediaPlugin: MediaPlugin<T>)
}