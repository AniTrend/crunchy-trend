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
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.session.datasource.remote.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionEntity
import co.anitrend.support.crunchyroll.data.session.mapper.SessionResponseMapper
import co.anitrend.support.crunchyroll.data.session.source.contract.SessionSource
import co.anitrend.support.crunchyroll.data.session.transformer.SessionTransformer
import co.anitrend.support.crunchyroll.domain.session.entities.Session
import co.anitrend.support.crunchyroll.domain.session.models.CrunchyUnBlockedSessionQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber

class UnblockSessionSourceImpl(
    private val dao: CrunchySessionDao,
    private val loginDao: CrunchyLoginDao,
    private val settings: IAuthenticationSettings,
    private val endpoint: CrunchySessionEndpoint,
    private val coreSessionDao: CrunchySessionCoreDao,
    private val mapper: SessionResponseMapper,
    private val supportConnectivity: SupportConnectivity,
    supportDispatchers: SupportDispatchers
) : SessionSource(supportDispatchers) {

    private suspend fun buildQuery(): CrunchyUnBlockedSessionQuery? {
        val coreSession = coreSessionDao.findBySessionId(
            settings.sessionId
        )
        val loginSession = loginDao.findByUserId(
            settings.authenticatedUserId
        )

        if (coreSession != null && loginSession != null) {
            return CrunchyUnBlockedSessionQuery(
                auth = loginSession.auth,
                userId = loginSession.userId
            )
        }

        Timber.tag(moduleTag).e(
            "All sessions seem to be invalid, proceeding requests may fail"
        )
        networkState.postValue(
            NetworkState.Error(
                heading = "Missing session credentials",
                message = "Operation failed due to an error in the application code"
            )
        )

        return null
    }

    private suspend fun createNewUnblockSession(
        query: CrunchyUnBlockedSessionQuery
    ): CrunchySessionEntity? {
        val invalidSession = withContext(dispatchers.io) {
            val sessionId = settings.sessionId
            dao.findBySessionId(sessionId)
        }

        val deferred = async {
            endpoint.startUnblockedSession(
                auth = query.auth,
                userId = query.userId
            )
        }

        val controller =
            mapper.controller(
                dispatchers,
                OnlineControllerPolicy.create(
                    supportConnectivity
                )
            )

        val session = controller(deferred, networkState)

        withContext(dispatchers.io) {
            if (session != null) {
                settings.sessionId = session.sessionId
                Timber.tag(moduleTag).d(
                    "Persisting unblock session into private store -> $session"
                )
            } else if (invalidSession != null) {
                dao.delete(invalidSession)
                Timber.tag(moduleTag).d(
                    "Removing previous invalid unblock session -> $invalidSession"
                )
            }
        }

        return session
    }

    /**
     * Handles the requesting data from a the network source and returns
     * [NetworkState] to the caller after execution
     */
    override fun invoke(): Session? {
        super.invoke()
        networkState.postValue(NetworkState.Loading)
        return runBlocking {
            val unblockSessionQuery =
                withContext(dispatchers.io) {
                    buildQuery()
                }

            unblockSessionQuery?.let {
                val session = createNewUnblockSession(it)
                SessionTransformer.transform(session)
            }
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        dao.clearTable()
    }
}