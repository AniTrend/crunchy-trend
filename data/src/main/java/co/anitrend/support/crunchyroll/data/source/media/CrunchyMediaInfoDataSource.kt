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

package co.anitrend.support.crunchyroll.data.source.media

import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.mapper.media.CrunchyMediaInfoMapper
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaInfoUseCase
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.source.core.SupportCoreDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CrunchyMediaInfoDataSource(
    parentCoroutineJob: Job? = null,
    private val mediaEndpoint: CrunchyMediaEndpoint,
    private val mediaDao: CrunchyMediaDao,
    private val payload: CrunchyMediaInfoUseCase.Payload
) : SupportCoreDataSource(parentCoroutineJob) {

    /**
     * Dispatches work for the paging data source to respective workers or mappers
     * that publish the result to any [androidx.lifecycle.LiveData] observers
     *
     * @see networkState
     */
    override fun invoke() {
        super.invoke()
        val futureResponse = async {
            mediaEndpoint.getMediaInfo(
                mediaId = payload.mediaId
            )
        }

        val mapper = CrunchyMediaInfoMapper(
            parentJob = supervisorJob,
            mediaDao = mediaDao
        )

        launch {
            mapper.handleResponse(futureResponse)
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override fun clearDataSource() {
        launch {
            mediaDao.clearTable()
        }
    }

    val mediaInfo =
        object : ISourceObservable<CrunchyMedia?, CrunchyMediaInfoUseCase.Payload> {
        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param parameter parameters, implementation is up to the developer
         */
        override fun invoke(parameter: CrunchyMediaInfoUseCase.Payload): LiveData<CrunchyMedia?> {
            invoke()
            return mediaDao.findByMediaIdX(
                mediaId = parameter.mediaId.toString()
            )
        }
    }
}