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
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyModelField
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.stream.StreamController
import co.anitrend.support.crunchyroll.data.stream.converters.StreamEntityConverter
import co.anitrend.support.crunchyroll.data.stream.datasource.local.CrunchyStreamDao
import co.anitrend.support.crunchyroll.data.stream.datasource.remote.CrunchyStreamEndpoint
import co.anitrend.support.crunchyroll.data.stream.helper.StreamCacheHelper
import co.anitrend.support.crunchyroll.data.stream.source.contract.CrunchyStreamSource
import co.anitrend.support.crunchyroll.domain.stream.models.CrunchyMediaStreamQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class CrunchyStreamSourceImpl(
    private val controller: StreamController,
    private val streamDao: CrunchyStreamDao,
    private val endpoint: CrunchyStreamEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    private val cache: StreamCacheHelper,
    override val dispatcher: ISupportDispatcher
) : CrunchyStreamSource() {

    override fun observable(query: CrunchyMediaStreamQuery) =
        streamDao.findStreamByMediaIdFlow(query.mediaId)
            .flowOn(dispatcher.io)
            .filterNotNull()
            .map { StreamEntityConverter.convertFrom(it) }

    override suspend fun getMediaStream(
        query: CrunchyMediaStreamQuery,
        callback: RequestCallback
    ) {
        if (cache.shouldRefreshStream(query.mediaId)) {
            val deferred = async {
                endpoint.getStreamInfo(
                    mediaId = query.mediaId,
                    mediaFields = CrunchyModelField.streamFields
                )
            }

            val result = controller(deferred, callback) {
                val data = it.data?.copy(mediaId = query.mediaId)
                it.copy(data = data)
            }
            if (result != null)
                cache.updateLastRequest(result.mediaId)
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     *
     * @param context Dispatcher context to run in
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            withContext(context) {
                val mediaId = query.mediaId
                streamDao.clearTableById(mediaId)
                cache.invalidateLastRequest(mediaId)
            }
        }
    }
}