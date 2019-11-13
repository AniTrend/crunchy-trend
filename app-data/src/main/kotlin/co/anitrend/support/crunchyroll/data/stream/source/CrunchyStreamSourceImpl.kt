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

package co.anitrend.support.crunchyroll.data.stream.source

import androidx.lifecycle.LiveData
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyMediaField
import co.anitrend.support.crunchyroll.data.stream.datasource.remote.CrunchyStreamEndpoint
import co.anitrend.support.crunchyroll.data.stream.source.contract.CrunchyStreamSource
import co.anitrend.support.crunchyroll.data.stream.mapper.CrunchyStreamResponseMapper
import co.anitrend.support.crunchyroll.data.transformer.MediaStreamTransformer
import co.anitrend.support.crunchyroll.domain.stream.models.MediaStreamQuery
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CrunchyStreamSourceImpl(
    private val endpoint: CrunchyStreamEndpoint,
    private val responseMapper: CrunchyStreamResponseMapper
) : CrunchyStreamSource() {

    override fun getMediaStream(query: MediaStreamQuery): LiveData<List<MediaStream>?> {
        retry = { getMediaStream(query) }
        networkState.value = NetworkState.Loading
        val deferred = async {
            endpoint.getStreamInfo(
                mediaId = query.mediaId,
                mediaFields = CrunchyMediaField.streamFields
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