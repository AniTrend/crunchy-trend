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

package co.anitrend.support.crunchyroll.data.repository.media

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import co.anitrend.support.crunchyroll.data.source.media.CrunchyMediaDataSource
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.UiModel
import io.wax911.support.data.repository.SupportRepository

class CrunchyMediaRepository(
    private val mediaEndpoint: CrunchyMediaEndpoint,
    private val mediaDao: CrunchyMediaDao
) : SupportRepository<PagedList<CrunchyMedia>>() {

    /**
     * Handles dispatching of network requests to a background thread
     *
     * @param bundle bundle of parameters for the request
     */
    override fun invokeRequest(bundle: Bundle): UiModel<PagedList<CrunchyMedia>> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val dataSource = CrunchyMediaDataSource(
            bundle = bundle,
            parentJob = supervisorJob,
            mediaEndpoint = mediaEndpoint,
            mediaDao = mediaDao
        )

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            MutableLiveData<NetworkState>()
        }

        return UiModel(
            model = dataSource.media.observerOnLiveDataWith(bundle),
            networkState = dataSource.networkState,
            refresh = {
                dataSource.refreshOrInvalidate()
                refreshTrigger.value = null
            },
            refreshState = refreshState,
            retry = {
                dataSource.retryFailedRequest()
            }
        )
    }

}