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
import co.anitrend.support.crunchyroll.navigation.*
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.feature.player.model.stream.MediaStreamItem
import co.anitrend.support.crunchyroll.feature.player.model.stream.MediaStreamWithExtras

class StreamPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    fun toStreamItem(
        stream: MediaStream,
        payload: MediaPlayer.Payload?
    ): MediaStreamItem {
        return MediaStreamItem.transform(
            MediaStreamWithExtras(
                mediaTitle = payload?.episodeTitle,
                mediaSubTitle = payload?.collectionName,
                mediaArtWorkThumbnail = payload?.collectionThumbnail,
                mediaThumbnail = payload?.episodeThumbnail,
                mediaStream = stream
            )
        )
    }
}