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
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.media.datasource.local.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.media.datasource.remote.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.media.mapper.MediaResponseMapper
import co.anitrend.support.crunchyroll.data.media.source.contract.MediaSource
import co.anitrend.support.crunchyroll.data.media.transformer.MediaTransformer
import co.anitrend.support.crunchyroll.domain.collection.models.CrunchyCollectionQuery
import co.anitrend.support.crunchyroll.domain.media.entities.CrunchyMedia
import co.anitrend.support.crunchyroll.domain.media.models.CrunchyMediaQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MediaSourceImpl(
    private val mapper: MediaResponseMapper,
    private val mediaDao: CrunchyMediaDao,
    private val mediaEndpoint: CrunchyMediaEndpoint,
    private val supportConnectivity: SupportConnectivity,
    supportDispatchers: SupportDispatchers
) : MediaSource(supportDispatchers) {

    fun getMediaForCollection(
        callback: PagingRequestHelper.Request.Callback,
        param: CrunchyMediaQuery
    ) {
        val deferred = async {
            mediaEndpoint.getMediaList(
                offset = supportPagingHelper.pageOffset,
                limit = supportPagingHelper.pageSize,
                collectionId = param.collectionId
            )
        }

        launch {
            val controller =
                mapper.controller(supportConnectivity, dispatchers)

            controller(deferred, callback)
        }
    }

    override val mediaObservable =
        object : ISourceObservable<CrunchyMediaQuery, PagedList<CrunchyMedia>> {

            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: CrunchyMediaQuery): LiveData<PagedList<CrunchyMedia>> {
                executionTarget = { getMediaForCollection(it, parameter) }

                val localSource =
                    mediaDao.findByCollectionIdFactory(parameter.collectionId)

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
        mediaDao.clearTable()
    }
}