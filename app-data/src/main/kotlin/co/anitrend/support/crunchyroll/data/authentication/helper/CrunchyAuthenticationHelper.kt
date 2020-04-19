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

package co.anitrend.support.crunchyroll.data.authentication.helper

import co.anitrend.arch.data.auth.contract.ISupportAuthentication
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.extension.toCrunchyLocale
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.locale.helper.ICrunchySessionLocale
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.session.transformer.CoreSessionTransformer
import co.anitrend.support.crunchyroll.data.session.transformer.SessionTransformer
import co.anitrend.support.crunchyroll.domain.session.entities.Session
import co.anitrend.support.crunchyroll.domain.session.interactors.CoreSessionUseCase
import co.anitrend.support.crunchyroll.domain.session.interactors.UnblockSessionUseCase
import okhttp3.Request
import timber.log.Timber

internal class CrunchyAuthenticationHelper(
    private val connectivityHelper: SupportConnectivity,
    private val coreSessionUseCase: CoreSessionUseCase,
    private val unblockSessionUseCase: UnblockSessionUseCase,
    private val sessionDao: CrunchySessionDao,
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val settings: IAuthenticationSettings,
    private val sessionLocale: ICrunchySessionLocale
): ISupportAuthentication {

    override val moduleTag: String = javaClass.simpleName

    /**
     * Facade to provide information on auth status of the application,
     * on demand
     */
    override val isAuthenticated: Boolean
        get() = settings.isAuthenticated

    private fun hasSessionExpired(sessionExpiryTime: Long?): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime > (sessionExpiryTime ?: 0)
    }

    private suspend fun getUnblockSession(forceRefresh: Boolean = false): Session? {
        val unblockSession = sessionDao.findBySessionId(
            settings.sessionId
        )
        val expired = hasSessionExpired(unblockSession?.expiresAt)
        val unblock = if (forceRefresh) {
            Timber.tag(moduleTag).d(
                "Force refresh requested for unblock session -> ${unblockSession?.sessionId} | hasExpired: $expired"
            )
            val session = unblockSessionUseCase()
            Timber.tag(moduleTag).d("Refreshed unblock session from remote source -> $session")
            session
        } else {
            val session = SessionTransformer.transform(unblockSession)
            Timber.tag(moduleTag).d(
                "Using existing core session from local source -> ${unblockSession?.sessionId} | hasExpired: $expired"
            )
            session
        }

        if (unblock == null)
            Timber.tag(moduleTag).w(
                "Unblock session entity is null, proceeding requests may fail!"
            )

        return unblock
    }

    private suspend fun getCoreSession(forceRefresh: Boolean = false): Session? {
        val coreSession = sessionCoreDao.findBySessionId(settings.sessionId)
        val core = if (forceRefresh) {
            Timber.tag(moduleTag).d("Force refresh requested for core session -> $coreSession")
            val session = coreSessionUseCase()
            Timber.tag(moduleTag).d("Refreshed core session from remote source -> ${session?.sessionId}")
            session
        } else {
            val session = CoreSessionTransformer.transform(coreSession)
            Timber.tag(moduleTag).d("Using existing core session from local source -> ${session?.sessionId}")
            session
        }

        if (core == null)
            Timber.tag(moduleTag).w(
                "Core session entity is null, proceeding requests may fail!"
            )

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
            Timber.tag(moduleTag).d("Added parameters to request query with session -> ${session.sessionId}")
        }
        else
            Timber.tag(moduleTag).w("No parameters added to request query because session is null")

        return request.newBuilder()
            .url(urlBuilder.build())
    }

    /**
     * Handles complex task or dispatching of token refreshing to the an external work,
     * optionally the implementation can perform these operation internally
     */
    @Synchronized
    internal suspend fun refreshSession(request: Request): Request.Builder {
        val session = if (isAuthenticated)
            getUnblockSession(forceRefresh = true)
        else
            getCoreSession(forceRefresh = true)

        return buildRequest(request, session)
    }

    /**
     * Injects additional query parameters
     *
     * @param request the request to build upon
     */
    @Synchronized
    internal suspend fun injectQueryParameters(request: Request): Request.Builder {
        return if (isAuthenticated) {
            val session = getUnblockSession()
            buildRequest(request, session)
        } else {
            val session = getCoreSession()
            buildRequest(request, session)
        }
    }

    /**
     * Handle invalid token state by either renewing it or un-authenticates
     * the user locally if the token cannot be refreshed
     */
    @Synchronized
    internal suspend fun invalidateSession() {
        if (connectivityHelper.isConnected) {
            settings.authenticatedUserId = IAuthenticationSettings.INVALID_USER_ID
            settings.isAuthenticated = false
            settings.sessionId = null
            sessionCoreDao.clearTable()
            sessionDao.clearTable()
            Timber.tag(moduleTag).w(
                "All sessions and authentication states have been invalidated/reset!"
            )
        }
    }

    companion object {
        private const val DEVICE_TYPE = "device_type"
        private const val DEVICE_ID = "device_id"
        private const val SESSION_ID = "session_id"
        private const val LOCALE = "locale"
    }
}