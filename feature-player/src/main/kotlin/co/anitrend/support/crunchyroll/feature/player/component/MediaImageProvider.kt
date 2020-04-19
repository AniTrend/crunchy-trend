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

package co.anitrend.support.crunchyroll.feature.player.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.model.stream.MediaStreamItem
import coil.ImageLoader
import coil.api.load
import coil.target.Target
import com.devbrackets.android.playlistcore.components.image.ImageProvider
import org.koin.core.KoinComponent
import org.koin.core.inject

class MediaImageProvider(
    private val context: Context,
    private val onImageUpdated: () -> Unit
) : ImageProvider<MediaStreamItem>, KoinComponent {

    private val imageLoader by inject<ImageLoader>()
    private val notificationImageTarget = NotificationImageTarget()
    private val remoteViewImageTarget = RemoteViewImageTarget()

    private val defaultNotificationImage: Bitmap by lazy {
        BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
    }


    private var notificationImage: Bitmap? = null

    /**
     * Retrieves the image that will be displayed as the remote view artwork
     * for the currently playing item.
     */
    override var remoteViewArtwork: Bitmap? = null
        private set

    /**
     * Retrieves the image that will be displayed in the notification to represent
     * the currently playing item.
     */
    override val largeNotificationImage: Bitmap?
        get() = if (notificationImage != null) notificationImage else defaultNotificationImage

    /**
     * Retrieves the Drawable resource that specifies the icon to place in the
     * status bar for the media playback notification.
     */
    override val notificationIconRes: Int
        get() = R.mipmap.ic_launcher


    /**
     * Retrieves the Drawable resource that specifies the icon to place on the
     * lock screen to indicate the app the owns the content being displayed.
     */
    override val remoteViewIconRes: Int
        get() = R.mipmap.ic_launcher

    /**
     * Called when the notification and remote view artwork needs to be updated
     * due to a playlist item change
     */
    override fun updateImages(playlistItem: MediaStreamItem) {
        imageLoader.load(context, playlistItem.thumbnailUrl) {
            target(notificationImageTarget)
        }
        imageLoader.load(context, playlistItem.artworkUrl) {
            target(remoteViewImageTarget)
        }
    }

    /**
     * A class used to listen to the loading of the large notification images and perform
     * the correct functionality to update the notification once it is loaded.
     *
     * **NOTE:** This is a Glide Image loader class
     */
    private inner class NotificationImageTarget : Target {

        /**
         * Called if the image request is successful.
         */
        override fun onSuccess(result: Drawable) {
            super.onSuccess(result)
            notificationImage = result.toBitmap()
            onImageUpdated()
        }
    }

    /**
     * A class used to listen to the loading of the large lock screen images and perform
     * the correct functionality to update the artwork once it is loaded.
     *
     * **NOTE:** This is a Glide Image loader class
     */
    private inner class RemoteViewImageTarget : Target {

        /**
         * Called if the image request is successful.
         */
        override fun onSuccess(result: Drawable) {
            super.onSuccess(result)
            remoteViewArtwork = result.toBitmap()
            onImageUpdated()
        }
    }
}