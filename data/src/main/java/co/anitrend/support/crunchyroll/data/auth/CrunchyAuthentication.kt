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
import co.anitrend.arch.extension.util.SupportConnectivityHelper
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.extension.getSerivceLocale
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import co.anitrend.support.crunchyroll.domain.entities.result.session.Session
import co.anitrend.support.crunchyroll.domain.repositories.session.ISessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import timber.log.Timber

class CrunchyAuthentication(
    private val connectivityHelper: SupportConnectivityHelper,
    private val repository: ISessionRepository,
    private val sessionDao: CrunchySessionDao,
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val settings: CrunchySettings
): ISupportAuthentication {

    override val moduleTag: String = javaClass.simpleName
    /**
     * Facade to provide information on auth status of the application,
     * on demand
     */
    override val isAuthenticated: Boolean
        get() = settings.isAuthenticated

    /**
     * Checks if the data source that contains the token is valid
     */
    private suspend fun isSessionValid(): Boolean {
        val session = getUnblockSession()
        return session != null && !session.hasExpired()
    }

    private suspend fun getUnblockSession(): CrunchySession? {
        var session = sessionDao.findLatest()
        if (session == null) {
            if (isAuthenticated) {
                val unblock = repository.getUnblockedSession()
                Timber.d("New session retrieved with id: ${unblock?.sessionId}")
                session = sessionDao.findLatest()
            }
        }

        if (session == null)
            Timber.tag(moduleTag).w(
                "Not authenticated, did you intend to call getUnblockSession?"
            )
        else
            Timber.d("Session being used with id: ${session.session_id}")

        return session
    }

    suspend fun getCoreSession(): CrunchySessionCore? {
        var session = sessionCoreDao.findLatest()
        if (session == null) {
            val core = repository.getCoreSession()
            Timber.d("New session retrieved with id: ${core?.sessionId}")
            session = sessionCoreDao.findLatest()
        }

        if (session == null)
            Timber.tag(moduleTag).w(
                "Session is not available, please check your connection"
            )
        else
            Timber.d("Session being used with id: ${session.session_id}")

        return session
    }

    /**
     * Handles complex task or dispatching of token refreshing to the an external work,
     * optionally the implementation can perform these operation internally
     */
    @Synchronized
    suspend fun refreshSession(request: Request): Request.Builder {
        if (isAuthenticated && !isSessionValid()) {
            val session = repository.getUnblockedSession()
            if (session == null)
                Timber.tag(moduleTag).w("Failed to obtain new user session")
        } else {
            val session = repository.getCoreSession()
            if (session == null)
                Timber.tag(moduleTag).w("Failed to obtain application session")
        }
        return injectQueryParameters(request)
    }


    /**
     * Injects auth headers if the application was authenticated,
     * otherwise non
     *
     * @param request
     */
    @Synchronized
    suspend fun injectQueryParameters(request: Request): Request.Builder {
        val originalHttpUrl = request.url
        val urlBuilder = originalHttpUrl.newBuilder()
            .addEncodedQueryParameter(LOCALE, getSerivceLocale())

        if (isAuthenticated) {
            val session = getUnblockSession()
            if (session != null) {
                urlBuilder
                    .addEncodedQueryParameter(DEVICE_ID, session.device_id)
                    .addEncodedQueryParameter(DEVICE_TYPE, session.device_type)
                    .addEncodedQueryParameter(SESSION_ID, session.session_id)
                Timber.tag(moduleTag).d(
                    """
                    Adding query parameters for authenticated 
                    session and expires at: ${session.expires}
                """.trimIndent()
                )
            } else
                Timber.tag(moduleTag).w(
                    """
                    Session for current user not present, unable to dynamically inject parameters
                """.trimIndent()
                )
        } else {
            val session = getCoreSession()
            if (session != null) {
                urlBuilder
                    .addEncodedQueryParameter(DEVICE_ID, session.device_id)
                    .addEncodedQueryParameter(DEVICE_TYPE, session.device_type)
                    .addEncodedQueryParameter(SESSION_ID, session.session_id)
            } else
                Timber.tag(moduleTag).w(
                    """
                    Session for application not present, unable to dynamically inject parameters
                """.trimIndent()
                )
        }

        return request.newBuilder()
            .url(urlBuilder.build())
    }

    /**
     * Handle invalid token state by either renewing it or un-authenticates
     * the user locally if the token cannot be refreshed
     */
    @Synchronized
    suspend fun onInvalidToken() {
        if (connectivityHelper.isConnected) {
            settings.authenticatedUserId = CrunchySettings.INVALID_USER_ID
            settings.isAuthenticated = false
            sessionCoreDao.clearTable()
            sessionDao.clearTable()
            Timber.tag(moduleTag)
                .e("Authentication token is null, application is logging user out!")
        }
    }

    companion object {
        private const val DEVICE_TYPE = "device_type"
        private const val DEVICE_ID = "device_id"
        private const val SESSION_ID = "session_id"
        private const val LOCALE = "locale"
    }
}