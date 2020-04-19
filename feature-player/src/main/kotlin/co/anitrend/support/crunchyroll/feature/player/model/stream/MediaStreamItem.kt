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

package co.anitrend.support.crunchyroll.feature.player.model.stream

import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.domain.stream.enums.CrunchyStreamQuality
import com.devbrackets.android.playlistcore.api.PlaylistItem
import com.devbrackets.android.playlistcore.manager.BasePlaylistManager

class MediaStreamItem(
    val mediaTitle: String?,
    val mediaSubTitle: String?,
    val mediaStreamQuality: CrunchyStreamQuality,
    val mediaPlayHead: Int,
    override val album: String?,
    override val artist: String?,
    override val artworkUrl: String?,
    override val downloaded: Boolean,
    override val downloadedMediaUri: String?,
    override val id: Long,
    override val mediaType: Int,
    override val mediaUrl: String?,
    override val thumbnailUrl: String?,
    override val title: String?
) : PlaylistItem {

    companion object : ISupportMapperHelper<MediaStreamWithExtras, MediaStreamItem> {
        /**
         * Transforms the the [source] to the target type
         */
        override fun transform(source: MediaStreamWithExtras): MediaStreamItem {
            return MediaStreamItem(
                mediaTitle = source.mediaTitle,
                mediaSubTitle = source.mediaSubTitle,
                mediaStreamQuality = source.mediaStream.quality,
                mediaPlayHead = source.mediaStream.playHead,
                album = null,
                artist = null,
                artworkUrl = source.mediaArtWorkThumbnail,
                downloaded = false,
                downloadedMediaUri = null,
                id = 0,
                mediaType = BasePlaylistManager.VIDEO,
                mediaUrl = source.mediaStream.url,
                thumbnailUrl = source.mediaThumbnail,
                title = source.mediaTitle
            )
        }
    }
}