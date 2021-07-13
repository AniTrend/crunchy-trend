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

import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.model.Request
import co.anitrend.arch.domain.entities.RequestError
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.session.SessionController
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionEntity
import co.anitrend.support.crunchyroll.data.session.source.contract.SessionSource
import co.anitrend.support.crunchyroll.data.session.transformer.SessionTransformer
import co.anitrend.support.crunchyroll.domain.session.entities.Session
import co.anitrend.support.crunchyroll.domain.session.models.CrunchyNormalSessionQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class NormalSessionSourceImpl(
    private val dao: CrunchySessionDao,
    private val settings: IAuthenticationSettings,
    private val loginDao: CrunchyLoginDao,
    private val endpoint: CrunchyAuthenticationEndpoint,
    private val coreSessionDao: CrunchySessionCoreDao,
    private val controller: SessionController,
    override val dispatcher: ISupportDispatcher
) : SessionSource() {

    private suspend fun buildQuery(callback: RequestCallback): CrunchyNormalSessionQuery? {
        val coreSession = coreSessionDao.findBySessionId(
            settings.sessionId.value
        )
        val loginSession = loginDao.findByUserId(
            settings.authenticatedUserId.value
        )

        if (coreSession != null && loginSession != null) {
            return CrunchyNormalSessionQuery(
                auth = loginSession.auth,
                deviceType = coreSession.deviceType,
                deviceId = coreSession.deviceId
            )
        }

        Timber.e("All sessions seem to be invalid, proceeding requests may fail")

        callback.recordFailure(
            RequestError(
                "Missing session credentials",
                "Operation failed due to an error in the application code",
                null
            )
        )

        return null
    }

    private suspend fun createNewNormalSession(
        query: CrunchyNormalSessionQuery,
        callback: RequestCallback
    ): CrunchySessionEntity? {
        val invalidSession = withContext(dispatcher.io) {
            val sessionId = settings.sessionId.value
            dao.findBySessionId(sessionId)
        }

        val deferred = async {
            endpoint.startNormalSession(
                auth = query.auth,
                deviceId = query.deviceType,
                deviceType = query.deviceType
            )
        }

        val session = controller(deferred, callback)

        withContext(dispatcher.io) {
            if (session != null) {
                settings.sessionId.value = session.sessionId
                Timber.d(
                    "Persisting normal session into private store -> $session"
                )
            } else if (invalidSession != null) {
                dao.delete(invalidSession)
                Timber.d(
                    "Removing previous invalid normal session -> $invalidSession"
                )
            }
        }

        return session
    }

    /**
     * Handles the requesting data from a the network source and returns
     * [NetworkState] to the caller after execution
     */
    override fun invoke() = flow {
        requestHelper.runIfNotRunning(
            Request.Default(
                "normal_session_source_initial",
                Request.Type.INITIAL
            )
        ) { callback ->
            val normalSessionQuery = withContext(dispatcher.io) {
                buildQuery(callback)
            }

            val result = normalSessionQuery?.let { sessionQuery ->
                createNewNormalSession(sessionQuery, callback)
            }

            emit(SessionTransformer.transform(result))
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        withContext(context) {
            dao.clearTable()
        }
    }
}