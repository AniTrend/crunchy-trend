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

package co.anitrend.support.crunchyroll.data.datasource.auto.media

import androidx.lifecycle.LiveData
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.arch.MediaFieldsContract
import co.anitrend.support.crunchyroll.data.datasource.auto.media.contract.MediaStreamSource
import co.anitrend.support.crunchyroll.data.mapper.media.MediaStreamResponseMapper
import co.anitrend.support.crunchyroll.data.transformer.MediaStreamTransformer
import co.anitrend.support.crunchyroll.domain.entities.query.media.MediaStreamQuery
import co.anitrend.support.crunchyroll.domain.entities.result.media.MediaStream
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MediaStreamSourceImpl(
    private val endpoint: CrunchyMediaEndpoint,
    private val responseMapper: MediaStreamResponseMapper
) : MediaStreamSource() {

    override fun getMediaStream(query: MediaStreamQuery): LiveData<List<MediaStream>?> {
        retry = { getMediaStream(query) }
        networkState.value = NetworkState.Loading
        val deferred = async {
            endpoint.getStreamInfo(
                mediaId = query.mediaId,
                mediaFields = MediaFieldsContract.streamFields
            )
        }

        launch {
            val result = responseMapper(deferred, networkState)

            observable.postValue(
                MediaStreamTransformer.transform(result)
            )
        }

        return observable
    }
}