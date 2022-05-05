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

import androidx.paging.PagedList
import co.anitrend.arch.data.paging.FlowPagedListBuilder
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.model.Request
import co.anitrend.arch.data.util.PAGING_CONFIGURATION
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyClearDataHelper
import co.anitrend.support.crunchyroll.data.arch.helper.CrunchyPagingConfigHelper
import co.anitrend.support.crunchyroll.data.media.MediaController
import co.anitrend.support.crunchyroll.data.media.datasource.local.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.media.datasource.remote.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.media.source.contract.MediaSource
import co.anitrend.support.crunchyroll.data.media.transformer.MediaTransformer
import co.anitrend.support.crunchyroll.domain.media.entities.CrunchyMedia
import co.anitrend.support.crunchyroll.domain.media.models.CrunchyMediaQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

internal class MediaSourceImpl(
    private val controller: MediaController,
    private val mediaDao: CrunchyMediaDao,
    private val endpoint: CrunchyMediaEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    override val dispatcher: ISupportDispatcher
) : MediaSource() {

    override fun observable(mediaQuery: CrunchyMediaQuery): Flow<PagedList<CrunchyMedia>> {
        val factory = mediaDao.findByCollectionIdFactory(mediaQuery.collectionId)
            .map { MediaTransformer.transform(it) }

        return FlowPagedListBuilder(
            dataSourceFactory = factory,
            config = PAGING_CONFIGURATION,
            initialLoadKey = null,
            boundaryCallback = this
        ).buildFlow()
    }

    override suspend fun invoke(
        callback: RequestCallback,
        request: Request,
        model: CrunchyMedia?
    ) {
        CrunchyPagingConfigHelper(request, supportPagingHelper) {
            mediaDao.countByCollectionId(query.collectionId)
        }

        val deferred = async {
            endpoint.getMediaList(
                offset = supportPagingHelper.pageOffset,
                limit = supportPagingHelper.pageSize,
                collectionId = query.collectionId
            )
        }

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