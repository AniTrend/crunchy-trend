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

package co.anitrend.support.crunchyroll.data.media.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.contract.IRequestHelper
import co.anitrend.arch.data.util.PAGING_CONFIGURATION
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyPagingConfigHelper
import co.anitrend.support.crunchyroll.data.media.datasource.local.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.media.datasource.remote.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.media.mapper.MediaResponseMapper
import co.anitrend.support.crunchyroll.data.media.source.contract.MediaSource
import co.anitrend.support.crunchyroll.data.media.transformer.MediaTransformer
import co.anitrend.support.crunchyroll.domain.media.entities.CrunchyMedia
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

internal class MediaSourceImpl(
    private val mapper: MediaResponseMapper,
    private val mediaDao: CrunchyMediaDao,
    private val endpoint: CrunchyMediaEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    supportDispatchers: SupportDispatchers
) : MediaSource(supportDispatchers) {

    override val observable = liveData {
        val localSource =
            mediaDao.findByCollectionIdFactory(query.collectionId)

        val result = localSource.map {
            MediaTransformer.transform(it)
        }

        emitSource(
            result.toLiveData(
                config = PAGING_CONFIGURATION,
                boundaryCallback = this@MediaSourceImpl
            )
        )
    }

    override suspend fun invoke(
        callback: RequestCallback,
        requestType: IRequestHelper.RequestType,
        model: CrunchyMedia?
    ) {
        CrunchyPagingConfigHelper(requestType, supportPagingHelper) {
            mediaDao.countByCollectionId(query.collectionId)
        }

        val deferred = async {
            endpoint.getMediaList(
                offset = supportPagingHelper.pageOffset,
                limit = supportPagingHelper.pageSize,
                collectionId = query.collectionId
            )
        }

        val controller =
            mapper.controller(
                dispatchers,
                OnlineControllerPolicy.create(
                    supportConnectivity
                )
            )

        controller(deferred, callback)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            withContext(context) {
                mediaDao.clearTableByCollectionId(query.collectionId)
            }
        }
    }
}