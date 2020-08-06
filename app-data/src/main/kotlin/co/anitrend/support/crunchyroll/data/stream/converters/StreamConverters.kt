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

package co.anitrend.support.crunchyroll.data.stream.converters

import co.anitrend.arch.data.converter.SupportConverter
import co.anitrend.support.crunchyroll.data.stream.entity.CrunchyStreamEntity
import co.anitrend.support.crunchyroll.data.stream.model.CrunchyStreamInfoModel
import co.anitrend.support.crunchyroll.data.util.extension.iso8601ToUnixTime
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.domain.stream.enums.CrunchyStreamQuality

internal object StreamModelConverter : SupportConverter<CrunchyStreamInfoModel, CrunchyStreamEntity>() {
    override val fromType: (CrunchyStreamInfoModel) -> CrunchyStreamEntity = { source ->
        val adaptiveStream = source.stream_data.streams.first {
            it.quality == CrunchyStreamQuality.adaptive
        }
        CrunchyStreamEntity(
            mediaId = 0,
            playHead = source.playhead,
            subtitleLanguage = source.stream_data.hardsub_lang,
            audioLanguage = source.stream_data.audio_lang,
            format = source.stream_data.format,
            quality = adaptiveStream.quality,
            expires = adaptiveStream.expires.iso8601ToUnixTime(),
            url = adaptiveStream.url
        )
    }

    override val toType: (CrunchyStreamEntity) -> CrunchyStreamInfoModel = {
        TODO()
    }
}

internal object StreamEntityConverter : SupportConverter<CrunchyStreamEntity, MediaStream>() {
    override val fromType: (CrunchyStreamEntity) -> MediaStream = { source ->
        MediaStream(
            subtitleLanguage = source.subtitleLanguage,
            audioLanguage = source.audioLanguage,
            format = source.format,
            quality = source.quality,
            url = source.url,
            playHead = source.playHead,
            expires = source.expires
        )
    }

    override val toType: (MediaStream) -> CrunchyStreamEntity = {
        TODO()
    }
}