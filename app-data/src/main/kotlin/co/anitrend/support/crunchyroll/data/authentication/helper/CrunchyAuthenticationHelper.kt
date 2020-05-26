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

import co.anitrend.arch.extension.empty
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyRequestInterceptor
import co.anitrend.support.crunchyroll.data.arch.extension.toCrunchyLocale
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.locale.helper.ICrunchySessionLocale
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.session.transformer.CoreSessionTransformer
import co.anitrend.support.crunchyroll.data.session.transformer.SessionTransformer
import co.anitrend.support.crunchyroll.domain.session.entities.Session
import co.anitrend.support.crunchyroll.domain.session.interactors.CoreSessionUseCase
import co.anitrend.support.crunchyroll.domain.session.interactors.NormalSessionUseCase
import co.anitrend.support.crunchyroll.domain.session.interactors.UnblockSessionUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import timber.log.Timber

internal class CrunchyAuthenticationHelper(
    private val connectivityHelper: SupportConnectivity,
    private val coreSessionUseCase: CoreSessionUseCase,
    private val normalSessionUseCase: NormalSessionUseCase,
    private val unblockSessionUseCase: UnblockSessionUseCase,
    private val sessionDao: CrunchySessionDao,
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val settings: IAuthenticationSettings,
    private val sessionLocale: ICrunchySessionLocale
) {

    private val moduleTag: String = javaClass.simpleName

    /**
     * Since traditional locking mechanisms in java don't work so well with kotlin coroutines
     * we're going to use mutex instead of synchronized locks.
     *
     * [Shared mutable state and concurrency](https://kotlinlang.org/docs/reference/coroutines/shared-mutable-state-and-concurrency.html)
     */
    private val mutex = Mutex()

    /**
     * Facade to provide information on auth status of the application,
     * on demand
     */
    private val isAuthenticated: Boolean
        get() = settings.isAuthenticated

    private fun hasSessionExpired(sessionExpiryTime: Long?): Boolean {
        val currentTime = System.currentTimeMillis()
        return currentTime > (sessionExpiryTime ?: 0)
    }

    private suspend fun getAuthSession(forceRefresh: Boolean = false): Session? {
        val unblockSession = sessionDao.findBySessionId(
            settings.sessionId
        )
        val expired = hasSessionExpired(unblockSession?.expiresAt)
        val unblock = if (forceRefresh) {
            Timber.tag(moduleTag).d(
                "Force refresh requested for auth session -> ${unblockSession?.sessionId} | hasExpired: $expired"
            )
            val session = unblockSessionUseCase() ?: normalSessionUseCase()
            Timber.tag(moduleTag).d("Refreshed auth session from remote source -> $session")
            session
        } else {
            val session = SessionTransformer.transform(unblockSession)
            Timber.tag(moduleTag).d(
                "Using existing auth session from local source -> ${unblockSession?.sessionId} | hasExpired: $expired"
            )
            session
        }

        if (unblock == null)
            Timber.tag(moduleTag).w(
                "Auth session entity is null, proceeding requests may fail!"
            )

        return unblock
    }

    private suspend fun getCoreSession(forceRefresh: Boolean = false): Session? {
        // How do we check if the session is truly invalid?
        val coreSession = sessionCoreDao.findBySessionId(settings.sessionId)
        val core = if (forceRefresh) {
            Timber.tag(moduleTag).d("Force refresh requested for core session -> $coreSession")
            val session = coreSessionUseCase()
            Timber.tag(moduleTag)
                .d("Refreshed core session from remote source -> ${session?.sessionId}")
            session
        } else {
            val session = CoreSessionTransformer.transform(coreSession)
            Timber.tag(moduleTag)
                .d("Using existing core session from local source -> ${session?.sessionId}")
            session
        }

        if (core == null)
            Timber.tag(moduleTag).w(
                "Core session entity is null, proceeding requests may fail!"
            )

        return core
    }

    private fun buildRequest(request: Request, session: Session?): Request.Builder {
        val requestBuilder = request.newBuilder()
        val urlBuilder = request.url.newBuilder()

        if (session != null) {
            when (request.method) {
                CrunchyRequestInterceptor.GET_METHOD -> {
                    urlBuilder.addEncodedQueryParameter(SESSION_ID, session.sessionId)
                        .addEncodedQueryParameter(
                            LOCALE,
                            sessionLocale.sessionLocale.toCrunchyLocale()
                        )
                    Timber.tag(moduleTag)
                        .d("Added parameters to request query with session -> ${session.sessionId}")
                }
                else -> {
                    val mimeType = request.body?.contentType()
                    val requestBody = request.body.bodyToString(moduleTag)
                    val formBody = FormBody.Builder()
                        .addEncoded(SESSION_ID, session.sessionId)
                        .build().bodyToString(moduleTag)

                    val body = ("${requestBody}&${formBody}")
                        .toRequestBody(mimeType)
                    requestBuilder.post(body)
                }
            }
        } else
            Timber.tag(moduleTag).w("No parameters added to request query because session is null")

        return requestBuilder.url(
            urlBuilder.build()
        )
    }

    /**
     * Handles complex task or dispatching of token refreshing to the an external work,
     * optionally the implementation can perform these operation internally
     */
    internal suspend fun refreshSession(request: Request, forceRefresh: Boolean = true) = mutex.withLock {
        val session = if (isAuthenticated)
            getAuthSession(forceRefresh)
        else
            getCoreSession(forceRefresh)

        buildRequest(request, session)
    }

    /**
     * Injects additional query parameters
     *
     * @param request the request to build upon
     */
    internal suspend fun injectQueryParameters(request: Request) = mutex.withLock {
        if (isAuthenticated) {
            val session = getAuthSession()
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
    internal suspend fun invalidateSession() {
        mutex.withLock {
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
    }

    companion object {
        private const val SESSION_ID = "session_id"
        private const val LOCALE = "locale"

        internal fun RequestBody?.bodyToString(tag: String) = runCatching {
            if (this == null)
                return@runCatching String.empty()
            val buffer = Buffer()
            writeTo(buffer)
            buffer.readUtf8()
        }.getOrElse {
            Timber.tag(tag).e(it)
            String.empty()
        }
    }
}