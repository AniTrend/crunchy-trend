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
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import androidx.paging.toLiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.mapper.media.CrunchyMediaListMapper
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import co.anitrend.support.crunchyroll.data.model.stream.CrunchyStreamInfo
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaListUseCase
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaStreamUseCase
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.contract.SupportStateContract
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.source.core.SupportCoreDataSource
import io.wax911.support.data.source.paging.SupportPagingDataSource
import io.wax911.support.data.util.SupportDataKeyStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class CrunchyMediaStreamDataSource(
    parentCoroutineJob: Job? = null,
    private val mediaEndpoint: CrunchyMediaEndpoint,
    private val payload: CrunchyMediaStreamUseCase.Payload
) : SupportCoreDataSource(parentCoroutineJob) {

    private val streamInfoLiveData = MutableLiveData<CrunchyStreamInfo?>()

    /**
     * Dispatches work for the paging data source to respective workers or mappers
     * that publish the result to any [androidx.lifecycle.LiveData] observers
     *
     * @see networkState
     */
    override fun invoke() {
        super.invoke()
        val futureResponse = async {
            mediaEndpoint.getStreamInfo(
                mediaId = payload.mediaId
            )
        }

        launch {
            CrunchyMapper.handleResponseUsing(
                deferred = futureResponse,
                networkState = networkState,
                liveData = streamInfoLiveData
            )
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override fun clearDataSource() {
        streamInfoLiveData.postValue(null)
    }

    val streamInfo =
        object : ISourceObservable<CrunchyStreamInfo?, Nothing?> {
        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param parameter parameters, implementation is up to the developer
         */
        override fun invoke(parameter: Nothing?): LiveData<CrunchyStreamInfo?> {
            if (streamInfoLiveData.value == null)
                invoke()
            return streamInfoLiveData
        }
    }
}