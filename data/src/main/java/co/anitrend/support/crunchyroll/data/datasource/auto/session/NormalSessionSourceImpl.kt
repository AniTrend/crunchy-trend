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

package co.anitrend.support.crunchyroll.data.datasource.auto.session

import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.datasource.auto.session.contract.SessionSource
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.mapper.session.SessionResponseMapper
import co.anitrend.support.crunchyroll.data.transformer.SessionTransformer
import co.anitrend.support.crunchyroll.data.util.extension.iso8601ToUnixTime
import co.anitrend.support.crunchyroll.domain.entities.query.session.NormalSessionQuery
import co.anitrend.support.crunchyroll.domain.entities.query.session.UnBlockedSessionQuery
import co.anitrend.support.crunchyroll.domain.entities.result.session.Session
import kotlinx.coroutines.async
import org.threeten.bp.Instant
import timber.log.Timber

class NormalSessionSourceImpl(
    private val dao: CrunchySessionDao,
    private val endpoint: CrunchyAuthEndpoint,
    private val coreSessionDao: CrunchySessionDao,
    private val loginDao: CrunchyLoginDao,
    private val responseMapper: SessionResponseMapper
) : SessionSource<Nothing?>() {

    private suspend fun buildQuery(): NormalSessionQuery? {
        val coreSession = coreSessionDao.findLatest()
        val loginSession = loginDao.findLatest()

        if (coreSession != null && loginSession != null) {
            return NormalSessionQuery(
                auth = loginSession.auth,
                deviceType = coreSession.device_type,
                deviceId = coreSession.device_id
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
    override suspend fun invoke(param: Nothing?): Session? {
        super.invoke(param)
        networkState.postValue(NetworkState.Loading)

        val query = buildQuery()

        if (query != null) {

            val deferred = async {
                endpoint.startNormalSession(
                    auth = query.auth,
                    deviceId = query.deviceType,
                    deviceType = query.deviceType
                )
            }

            responseMapper(deferred, networkState)

            val session = dao.findLatest()

            return SessionTransformer.transform(session)
        }

        return null
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        dao.clearTable()
    }
}