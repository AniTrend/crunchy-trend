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

package co.anitrend.support.crunchyroll.data.transformer

import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.data.model.stream.CrunchyStreamInfo
import co.anitrend.support.crunchyroll.data.util.extension.iso8601ToUnixTime
import co.anitrend.support.crunchyroll.domain.entities.result.media.MediaStream

object MediaStreamTransformer : ISupportMapperHelper<CrunchyStreamInfo?, List<MediaStream>?> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyStreamInfo?): List<MediaStream>? {
        return source?.stream_data?.streams?.map {
            MediaStream(
                quality = it.quality,
                url = it.url,
                playHead = source.playhead,
                expires = it.expires.iso8601ToUnixTime() ?: 0
            )
        }
    }
}