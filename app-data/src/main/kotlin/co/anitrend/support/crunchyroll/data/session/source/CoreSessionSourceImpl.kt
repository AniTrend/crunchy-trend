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
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyProperty
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.session.CoreSessionController
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.remote.CrunchyProxySessionEndpoint
import co.anitrend.support.crunchyroll.data.session.datasource.remote.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionCoreEntity
import co.anitrend.support.crunchyroll.data.session.source.contract.SessionSource
import co.anitrend.support.crunchyroll.data.session.transformer.CoreSessionTransformer
import co.anitrend.support.crunchyroll.data.util.extension.requireProperty
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

internal class CoreSessionSourceImpl(
    private val settings: IAuthenticationSettings,
    private val dao: CrunchySessionCoreDao,
    private val endpoint: CrunchySessionEndpoint,
    private val proxyEndpoint: CrunchyProxySessionEndpoint,
    private val controller: CoreSessionController,
    override val dispatcher: ISupportDispatcher
) : SessionSource() {

    private suspend fun createNewCoreSession(callback: RequestCallback): CrunchySessionCoreEntity? {
        val invalidSession = withContext(dispatcher.io) {
            val sessionId = settings.sessionId
            dao.findBySessionId(sessionId.value)
        }

        val proxySession = async {
            proxyEndpoint.startCoreSession()
        }

        val coreSession = async {
            endpoint.startCoreSessionJson(
                accessToken = requireProperty(CrunchyProperty.CLIENT_TOKEN),
                deviceType = requireProperty(CrunchyProperty.DEVICE_TYPE)
            )
        }

        val session = controller(proxySession, callback) ?: controller(coreSession, callback)

        withContext(dispatcher.io) {
            if (session != null) {
                settings.sessionId.value = session.sessionId
                Timber.d(
                    "Persisting core session into private storage space -> $session"
                )
            } else if (invalidSession != null) {
                dao.delete(invalidSession)
                Timber.d(
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
    override fun invoke() = flow {
        requestHelper.runIfNotRunning(
            Request.Default(
                "core_session_source_initial",
                Request.Type.INITIAL
            )
        ) {
            val result = createNewCoreSession(it)
            emit(CoreSessionTransformer.transform(result))
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