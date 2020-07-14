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
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.util.SupportDataKeyStore
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
import kotlinx.coroutines.async

internal class MediaSourceImpl(
    private val mapper: MediaResponseMapper,
    private val mediaDao: CrunchyMediaDao,
    private val endpoint: CrunchyMediaEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val settings: IRefreshBehaviourSettings,
    supportDispatchers: SupportDispatchers
) : MediaSource(supportDispatchers) {

    override suspend fun getMediaForCollection(
        callback: PagingRequestHelper.Request.Callback,
        requestType: PagingRequestHelper.RequestType,
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

    override val mediaObservable =
        object : ISourceObservable<Nothing?, PagedList<CrunchyMedia>> {

            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: Nothing?): LiveData<PagedList<CrunchyMedia>> {

                val localSource =
                    mediaDao.findByCollectionIdFactory(query.collectionId)

                val result = localSource.map {
                    MediaTransformer.transform(it)
                }

                return result.toLiveData(
                    config = SupportDataKeyStore.PAGING_CONFIGURATION,
                    boundaryCallback = this@MediaSourceImpl
                )
            }
        }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        CrunchyClearDataHelper(settings, supportConnectivity) {
            mediaDao.clearTableByCollectionId(query.collectionId)
        }
    }
}