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

package co.anitrend.support.crunchyroll.data.usecase.session

import android.os.Parcelable
import androidx.annotation.StringDef
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.source.session.CrunchySessionDataSource
import co.anitrend.support.crunchyroll.data.usecase.contract.IMappable
import co.anitrend.support.crunchyroll.data.usecase.session.contract.ISessionUseCasePayload
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.usecase.coroutine.ISupportCoroutineUseCase
import kotlinx.android.parcel.Parcelize

class CrunchySessionUseCase(
    private val sessionEndpoint: CrunchySessionEndpoint,
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val authEndpoint: CrunchyAuthEndpoint,
    private val sessionDao: CrunchySessionDao
) : ISupportCoroutineUseCase<CrunchySessionUseCase.Payload, NetworkState> {

    /**
     * Solves a given use case in the implementation target
     *
     * @param param input for solving a given use case
     */
    override suspend fun invoke(param: Payload): NetworkState {
        val dataSource = CrunchySessionDataSource(
            sessionEndpoint = sessionEndpoint,
            sessionCoreDao = sessionCoreDao,
            authEndpoint = authEndpoint,
            sessionDao = sessionDao,
            payload = param
        )

        return dataSource()
    }

    @Parcelize
    data class Payload(
        @RequestType
        override val sessionType: String?,
        override val deviceId: String?,
        override val deviceType: String?,
        val auth: String,
        val userId: Int?
    ) : ISessionUseCasePayload, Parcelable {

        override fun toMap() = mapOf(
            "auth" to auth,
            "user_id" to userId,
            "version" to version
        )

        fun onSessionFallBack() = object : IMappable {
            override fun toMap() = mapOf(
                "auth" to auth,
                "version" to version,
                "device_id" to deviceId,
                "device_type" to deviceType,
                "access_token" to accessToken
            )
        }

        @StringDef(
            RequestType.START_UNBLOCK_SESSION,
            RequestType.START_CORE_SESSION
        )
        annotation class RequestType {
            companion object {
                const val START_UNBLOCK_SESSION = "START_UNBLOCK_SESSION"
                const val START_CORE_SESSION = "START_CORE_SESSION"
            }
        }
    }
}