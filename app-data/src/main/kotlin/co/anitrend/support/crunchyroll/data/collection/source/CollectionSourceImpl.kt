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

package co.anitrend.support.crunchyroll.data.collection.source

import androidx.lifecycle.liveData
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
import co.anitrend.support.crunchyroll.data.collection.datasource.local.CrunchyCollectionDao
import co.anitrend.support.crunchyroll.data.collection.datasource.remote.CrunchyCollectionEndpoint
import co.anitrend.support.crunchyroll.data.collection.mapper.CollectionResponseMapper
import co.anitrend.support.crunchyroll.data.collection.source.contract.CollectionSource
import co.anitrend.support.crunchyroll.data.collection.transformer.CollectionTransformer
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

internal class CollectionSourceImpl(
    private val mapper: CollectionResponseMapper,
    private val collectionDao: CrunchyCollectionDao,
    private val collectionEndpoint: CrunchyCollectionEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    supportDispatchers: SupportDispatchers
) : CollectionSource(supportDispatchers) {

    override val observable = liveData {
        val localSource =
            collectionDao.findBySeriesIdFactory(query.seriesId)

        val result = localSource.map {
            CollectionTransformer.transform(it)
        }

        emitSource(
            result.toLiveData(
                config = PAGING_CONFIGURATION,
                boundaryCallback = this@CollectionSourceImpl
            )
        )
    }

    override suspend fun invoke(
        callback: RequestCallback,
        requestType: IRequestHelper.RequestType,
        model: CrunchyCollection?
    ) {
        CrunchyPagingConfigHelper(requestType, supportPagingHelper) {
            collectionDao.countBySeriesId(query.seriesId)
        }

        val deferred = async {
            collectionEndpoint.getCollections(
                offset = supportPagingHelper.pageOffset,
                limit = supportPagingHelper.pageSize,
                seriesId = query.seriesId
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
                val seriesId = query.seriesId
                collectionDao.clearTableById(seriesId)
            }
        }
    }
}