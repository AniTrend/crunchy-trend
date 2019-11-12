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

package co.anitrend.support.crunchyroll.data.auth

import co.anitrend.arch.data.auth.contract.ISupportAuthentication
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.extension.toCrunchyLocale
import co.anitrend.support.crunchyroll.data.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.transformer.CoreSessionTransformer
import co.anitrend.support.crunchyroll.data.transformer.SessionTransformer
import co.anitrend.support.crunchyroll.data.util.ICrunchySessionLocale
import co.anitrend.support.crunchyroll.domain.entities.result.session.Session
import co.anitrend.support.crunchyroll.domain.interactors.session.CoreSessionUseCase
import co.anitrend.support.crunchyroll.domain.interactors.session.UnblockSessionUseCase
import okhttp3.Request
import timber.log.Timber

class CrunchyAuthentication(
    private val connectivityHelper: SupportConnectivity,
    private val coreSessionUseCase: CoreSessionUseCase,
    private val unblockSessionUseCase: UnblockSessionUseCase,
    private val sessionDao: CrunchySessionDao,
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val settings: IAuthenticationSettings,
    private val sessionLocale: ICrunchySessionLocale
): ISupportAuthentication {

    override val moduleTag: String = javaClass.simpleName
    private val lock = Object()

    /**
     * Facade to provide information on auth status of the application,
     * on demand
     */
    override val isAuthenticated: Boolean
        get() = settings.isAuthenticated

    private fun getUnblockSession(forceRefresh: Boolean = false): Session? {
        val unblockSession = sessionDao.findBySessionId(
            settings.sessionId
        )
        val unblock = if (forceRefresh || unblockSession == null) {
            val session = unblockSessionUseCase()
            Timber.tag(moduleTag).d(
                "Refreshed unblock session from remote source with id: ${session?.sessionId}"
            )
            session
        } else {
            val session = SessionTransformer.transform(unblockSession)
            Timber.tag(moduleTag).d(
                "Using unblock session from local source with id: ${session?.sessionId}"
            )
            session
        }

        if (unblock == null)
            Timber.tag(moduleTag).d(
                "Session unblock is null!"
            )

        return unblock
    }

    private fun getCoreSession(forceRefresh: Boolean = false): Session? {
        val coreSession = sessionCoreDao.findBySessionId(
            settings.sessionId
        )
        val core = if (forceRefresh || coreSession == null) {
            val session = coreSessionUseCase()
            Timber.tag(moduleTag).d(
                "Refreshed core session from remote source with id: ${session?.sessionId}"
            )
            session
        } else {
            val session = CoreSessionTransformer.transform(coreSession)
            Timber.tag(moduleTag).d(
                "Using core session from local source with id: ${session?.sessionId}"
            )
            session
        }

        if (core == null)
            Timber.tag(moduleTag).d("Session core is null!")

        return core
    }

    private fun buildRequest(request: Request, session: Session?): Request.Builder {
        val originalHttpUrl = request.url
        val urlBuilder = originalHttpUrl.newBuilder()
            .addEncodedQueryParameter(LOCALE, sessionLocale.sessionLocale.toCrunchyLocale())

        if (session != null) {
            urlBuilder
                .addEncodedQueryParameter(DEVICE_ID, session.deviceId)
                .addEncodedQueryParameter(DEVICE_TYPE, session.deviceType)
                .addEncodedQueryParameter(SESSION_ID, session.sessionId)
            Timber.tag(moduleTag).d(
                """
                Adding query parameters for authenticated 
                session: ${session.sessionId}
            """.trimIndent()
            )
        }
        else
            Timber.tag(moduleTag).w("""
            Omitting build new url query due to missing or invalid session presented
        """.trimIndent())

        return request.newBuilder()
            .url(urlBuilder.build())
    }

    /**
     * Handles complex task or dispatching of token refreshing to the an external work,
     * optionally the implementation can perform these operation internally
     */
    fun refreshSession(request: Request): Request.Builder {
        synchronized(lock) {
            val session = if (isAuthenticated)
                getUnblockSession(forceRefresh = true)
            else
                getCoreSession(forceRefresh = true)

            return buildRequest(request, session)
        }
    }

    /**
     * Injects auth headers if the application was authenticated,
     * otherwise non
     *
     * @param request
     */
    fun injectQueryParameters(request: Request): Request.Builder {
        synchronized(lock) {
            return if (isAuthenticated) {
                val session = getUnblockSession()
                buildRequest(request, session)
            } else {
                val session = getCoreSession()
                buildRequest(request, session)
            }
        }
    }

    /**
     * Handle invalid token state by either renewing it or un-authenticates
     * the user locally if the token cannot be refreshed
     */
    fun invalidateSession() {
        synchronized(lock) {
            if (connectivityHelper.isConnected) {
                settings.authenticatedUserId = IAuthenticationSettings.INVALID_USER_ID
                settings.isAuthenticated = false
                sessionCoreDao.clearTable()
                sessionDao.clearTable()
                Timber.tag(moduleTag).e(
                    "All sessions are being invalidated and auth states are being reset!"
                )
            }
        }
    }

    companion object {
        private const val DEVICE_TYPE = "device_type"
        private const val DEVICE_ID = "device_id"
        private const val SESSION_ID = "session_id"
        private const val LOCALE = "locale"
    }
}