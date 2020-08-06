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
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyModelField
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.series.helper.SeriesCacheHelper
import co.anitrend.support.crunchyroll.data.stream.converters.StreamEntityConverter
import co.anitrend.support.crunchyroll.data.stream.datasource.local.CrunchyStreamDao
import co.anitrend.support.crunchyroll.data.stream.datasource.remote.CrunchyStreamEndpoint
import co.anitrend.support.crunchyroll.data.stream.helper.StreamCacheHelper
import co.anitrend.support.crunchyroll.data.stream.mapper.CrunchyStreamResponseMapper
import co.anitrend.support.crunchyroll.data.stream.source.contract.CrunchyStreamSource
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.domain.stream.models.CrunchyMediaStreamQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

internal class CrunchyStreamSourceImpl(
    private val mapper: CrunchyStreamResponseMapper,
    private val streamDao: CrunchyStreamDao,
    private val endpoint: CrunchyStreamEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    private val cache: StreamCacheHelper,
    supportDispatchers: SupportDispatchers
) : CrunchyStreamSource(supportDispatchers) {

    override val observable = flow {
        val localSource = streamDao.findStreamByMediaIdFlow(query.mediaId)
        val result = localSource.filterNotNull().map {
            StreamEntityConverter.convertFrom(it)
        }
        emitAll(result)
    }

    override suspend fun getMediaStream(
        query: CrunchyMediaStreamQuery,
        callback: RequestCallback
    ) {
        mapper.sourceMediaId = query.mediaId
        if (cache.shouldRefreshStream(query.mediaId)) {
            val deferred = async {
                endpoint.getStreamInfo(
                    mediaId = query.mediaId,
                    mediaFields = CrunchyModelField.streamFields
                )
            }

            val controller =
                mapper.controller(
                    dispatchers,
                    OnlineControllerPolicy.create(
                        supportConnectivity
                    )
                )

            val result = controller(deferred, callback)
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