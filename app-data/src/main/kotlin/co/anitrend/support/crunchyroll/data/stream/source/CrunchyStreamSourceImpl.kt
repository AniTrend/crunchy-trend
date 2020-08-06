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

import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OfflineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyModelField
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.stream.datasource.remote.CrunchyStreamEndpoint
import co.anitrend.support.crunchyroll.data.stream.mapper.CrunchyStreamResponseMapper
import co.anitrend.support.crunchyroll.data.stream.source.contract.CrunchyStreamSource
import co.anitrend.support.crunchyroll.data.stream.transformer.MediaStreamTransformer
import co.anitrend.support.crunchyroll.domain.stream.models.CrunchyMediaStreamQuery
import kotlinx.coroutines.async

internal class CrunchyStreamSourceImpl(
    private val endpoint: CrunchyStreamEndpoint,
    private val mapper: CrunchyStreamResponseMapper,
    private val supportConnectivity: SupportConnectivity,
    supportDispatchers: SupportDispatchers
) : CrunchyStreamSource(supportDispatchers) {

    override suspend fun getMediaStream(
        query: CrunchyMediaStreamQuery,
        callback: RequestCallback
    ) {
        val deferred = async {
            endpoint.getStreamInfo(
                mediaId = query.mediaId,
                mediaFields = CrunchyModelField.streamFields
            )
        }

        val controller =
            mapper.controller(
                dispatchers,
                OfflineControllerPolicy.create()
            )

        val result = controller(deferred, callback)

        observable.value = MediaStreamTransformer.transform(result)
    }
}