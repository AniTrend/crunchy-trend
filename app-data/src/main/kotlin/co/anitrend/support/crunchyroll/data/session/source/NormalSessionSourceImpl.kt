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

package co.anitrend.support.crunchyroll.data.session.source

import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.session.source.contract.SessionSource
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.session.mapper.SessionResponseMapper
import co.anitrend.support.crunchyroll.data.session.transformer.SessionTransformer
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.domain.session.models.CrunchyNormalSessionQuery
import co.anitrend.support.crunchyroll.domain.session.entities.Session
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class NormalSessionSourceImpl(
    private val dao: CrunchySessionDao,
    private val settings: IAuthenticationSettings,
    private val loginDao: CrunchyLoginDao,
    private val endpoint: CrunchyAuthenticationEndpoint,
    private val coreSessionDao: CrunchySessionCoreDao,
    private val responseMapper: SessionResponseMapper
) : SessionSource() {

    private fun buildQuery(): CrunchyNormalSessionQuery? {
        val coreSession = coreSessionDao.findBySessionId(
            settings.sessionId
        )
        val loginSession = loginDao.findByUserId(
            settings.authenticatedUserId
        )

        if (coreSession != null && loginSession != null) {
            return CrunchyNormalSessionQuery(
                auth = loginSession.auth,
                deviceType = coreSession.deviceType,
                deviceId = coreSession.deviceId
            )
        }

        Timber.tag(moduleTag).e("Core and login session may be null")
        networkState.postValue(
            NetworkState.Error(
                heading = "Missing Session Credentials",
                message = "Operation failed due to an error in the application code"
            )
        )

        return null
    }

    /**
     * Handles the requesting data from a the network source and returns
     * [NetworkState] to the caller after execution
     */
    override fun invoke(): Session? {
        super.invoke()
        networkState.postValue(NetworkState.Loading)

        return buildQuery()?.let { query ->
            val deferred = async {
                endpoint.startNormalSession(
                    auth = query.auth,
                    deviceId = query.deviceType,
                    deviceType = query.deviceType
                )
            }

            val session = runBlocking {
                responseMapper(deferred, networkState)
            }
            if (session != null)
                settings.sessionId = session.sessionId

            SessionTransformer.transform(session)
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        dao.clearTable()
    }
}