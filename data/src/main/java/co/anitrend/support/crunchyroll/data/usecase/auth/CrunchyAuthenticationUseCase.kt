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

package co.anitrend.support.crunchyroll.data.usecase.auth

import android.os.Parcelable
import androidx.annotation.StringDef
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyLogin
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.source.auth.CrunchyAuthDataSource
import co.anitrend.support.crunchyroll.data.usecase.contract.IMappable
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.UiModel
import io.wax911.support.data.usecase.core.ISupportCoreUseCase
import kotlinx.android.parcel.Parcelize

class CrunchyAuthenticationUseCase(
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val authEndpoint: CrunchyAuthEndpoint,
    private val loginDao: CrunchyLoginDao
) : ISupportCoreUseCase<CrunchyAuthenticationUseCase.Payload, UiModel<CrunchyLogin?>> {

    /**
     * Solves a given use case in the implementation target
     *
     * @param param input for solving a given use case
     */
    override fun invoke(param: Payload): UiModel<CrunchyLogin?> {
        val dataSource = CrunchyAuthDataSource(
            sessionCoreDao = sessionCoreDao,
            authEndpoint = authEndpoint,
            loginDao = loginDao,
            payload = param
        )

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            MutableLiveData<NetworkState>()
        }

        return UiModel(
            model = dataSource.authenticatedUser(null),
            networkState = dataSource.networkState,
            refresh = {
                dataSource.invalidateAndRefresh()
                refreshTrigger.value = null
            },
            refreshState = refreshState,
            retry = {
                dataSource.retryRequest()
            }
        )
    }

    @Parcelize
    data class Payload(
        val account: String,
        val password: String,
        val sessionId: String?,
        @RequestType
        val authenticationType: String
    ) : IMappable, Parcelable {
        override fun toMap() = mapOf(
            "account" to account,
            "password" to password,
            "session_id" to sessionId
        )

        @StringDef(
            RequestType.LOG_IN,
            RequestType.LOG_OUT
        )
        annotation class RequestType {
            companion object {
                const val LOG_IN = "LOG_IN"
                const val LOG_OUT = "LOG_OUT"

                const val authenticationPayload = "authenticationPayload"
            }
        }
    }
}