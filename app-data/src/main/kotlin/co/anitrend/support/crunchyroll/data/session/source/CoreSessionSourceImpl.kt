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
import co.anitrend.arch.data.request.contract.IRequestHelper
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.remote.CrunchyProxySessionEndpoint
import co.anitrend.support.crunchyroll.data.session.datasource.remote.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionCoreEntity
import co.anitrend.support.crunchyroll.data.session.mapper.CoreSessionResponseMapper
import co.anitrend.support.crunchyroll.data.session.model.CrunchySessionCoreModel
import co.anitrend.support.crunchyroll.data.session.source.contract.SessionSource
import co.anitrend.support.crunchyroll.data.session.transformer.CoreSessionTransformer
import co.anitrend.support.crunchyroll.domain.session.entities.Session
import kotlinx.coroutines.*
import retrofit2.Response
import timber.log.Timber

internal class CoreSessionSourceImpl(
    private val settings: IAuthenticationSettings,
    private val dao: CrunchySessionCoreDao,
    private val endpoint: CrunchySessionEndpoint,
    private val proxyEndpoint: CrunchyProxySessionEndpoint,
    private val mapper: CoreSessionResponseMapper,
    private val supportConnectivity: SupportConnectivity,
    supportDispatchers: SupportDispatchers
) : SessionSource(supportDispatchers) {

    private suspend fun requestCoreSession(
        deferred: Deferred<Response<CrunchyContainer<CrunchySessionCoreModel>>>,
        callback: RequestCallback
    ): CrunchySessionCoreEntity? {
        val controller =
            mapper.controller(
                dispatchers,
                OnlineControllerPolicy.create(
                    supportConnectivity
                )
            )

        return controller(deferred, callback)
    }

    private suspend fun createNewCoreSession(callback: RequestCallback): CrunchySessionCoreEntity? {
        val invalidSession = withContext(dispatchers.io) {
            val sessionId = settings.sessionId
            dao.findBySessionId(sessionId)
        }

        val session = requestCoreSession(
            async { proxyEndpoint.startCoreSession() }, callback
        ) ?: requestCoreSession(
            async { endpoint.startCoreSessionJson() }, callback
        )

        withContext(dispatchers.io) {
            if (session != null) {
                settings.sessionId = session.sessionId
                Timber.tag(moduleTag).d(
                    "Persisting core session into private storage space -> $session"
                )
            } else if (invalidSession != null) {
                dao.delete(invalidSession)
                Timber.tag(moduleTag).d(
                    "Removing previous invalid core session -> $invalidSession"
                )
            }
        }

        return session
    }

    /**
     * Handles the requesting data from a the network source and return
     * [NetworkState] to the caller after execution.
     *
     * In this context the super.invoke() method will allow a retry action to be set
     */
    override fun invoke(): Session? {
        var entity: CrunchySessionCoreEntity? = null
        runBlocking {
            requestHelper.runIfNotRunning(
                IRequestHelper.RequestType.INITIAL
            ) { callback ->
                entity = createNewCoreSession(callback)
            }
        }

        return CoreSessionTransformer.transform(entity)
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