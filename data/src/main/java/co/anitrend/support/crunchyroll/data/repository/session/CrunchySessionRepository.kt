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

package co.anitrend.support.crunchyroll.data.repository.session

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import co.anitrend.support.crunchyroll.data.api.endpoint.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.auth.model.contract.ICrunchySession
import co.anitrend.support.crunchyroll.data.source.session.CrunchySessionDataSource
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.UiModel
import io.wax911.support.data.repository.SupportRepository
import io.wax911.support.extension.util.SupportExtKeyStore

class CrunchySessionRepository(
    private val sessionEndpoint: CrunchySessionEndpoint,
    private val authEndpoint: CrunchyAuthEndpoint
) : SupportRepository<ICrunchySession?>() {


    /**
     * Handles dispatching of network requests to a background thread
     *
     * @param bundle bundle of parameters for the request
     */
    override fun invokeRequest(bundle: Bundle): UiModel<ICrunchySession?> {
        val dataSource = CrunchySessionDataSource(
            parentCoroutineJob = supervisorJob,
            sessionEndpoint = sessionEndpoint,
            authEndpoint = authEndpoint
        )

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            MutableLiveData<NetworkState>()
        }

        val sourceModel = when (bundle.getString(SupportExtKeyStore.arg_request_type)) {
            SessionRequestType.START_CORE_SESSION -> dataSource.coreSession.observerOnLiveDataWith(bundle)
            else -> dataSource.session.observerOnLiveDataWith(bundle)
        }

        return UiModel(
            model = Transformations.map(sourceModel) {
                it
            },
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