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

import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyUserDao
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import io.wax911.support.data.auth.SupportAuthentication
import io.wax911.support.extension.util.SupportConnectivityHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Request
import timber.log.Timber

class CrunchyAuthenticationHelper(
    private val sessionDao: CrunchySessionDao,
    private val connectivityHelper: SupportConnectivityHelper,
    private val settings: CrunchySettings,
    private val deviceToken: String
): SupportAuthentication<String>() {

    /**
     * Facade to provide information on authentication status of the application,
     * on demand
     */
    override val isAuthenticated: Boolean
        get() = settings.isAuthenticated

    /**
     * Checks if the data source that contains the token is valid
     */
    override fun isTokenValid(): Boolean {
        var token: CrunchySession? = null
        runBlocking {
            token = sessionDao.findLatest()
        }

        return when (token != null) {
            true -> {
                // TODO("Calculate token expiry time and check validity")
                true
            }
            else -> false
        }
    }

    /**
     * Handles complex task or dispatching of token refreshing to the an external work,
     * optionally the implementation can perform these operation internally
     */
    override fun refreshToken(): Boolean {
        // handle logic to refresh the token on the users behalf
        if (!isTokenValid())
            return false
        return true
    }

    /**
     * Injects authentication headers if the application was authenticated,
     * otherwise non
     *
     * @param requestBuilder
     */
    override fun injectHeaders(requestBuilder: Request.Builder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Injects authentication headers if the application was authenticated,
     * otherwise non
     *
     * @param request
     */
    fun injectHeaders(request: Request): Request.Builder {

        val originalHttpUrl = request.url()

        val urlBuilder = originalHttpUrl.newBuilder()

        if (isAuthenticated) {
            if (!isTokenValid())
                refreshToken()

            urlBuilder.addEncodedQueryParameter(ACCESS_TOKEN, deviceToken)
                .addEncodedQueryParameter(LOCALE, "enUS")

            runBlocking {
                sessionDao.findLatest()?.also {
                    urlBuilder
                        .addEncodedQueryParameter(DEVICE_ID, it.device_id )
                        .addEncodedQueryParameter(DEVICE_TYPE, it.device_type)
                        .addEncodedQueryParameter(SESSION_ID, it.session_id)
                }
            }

        }

        return request.newBuilder()
            .url(urlBuilder.build())
    }

    /**
     * If using Oauth 2 then once the user has approved your client they will be redirected to your redirect URI,
     * included in the URL fragment will be an access_token parameter that includes the JWT access token
     * used to make requests on their behalf.
     *
     * Otherwise this could just be an authentication result that should be handled and complete the authentication
     * process on the users behalf
     *
     * @param authPayload payload from an authenticating source
     * @return True if the operation was successful, false otherwise
     */
    override suspend fun handleCallbackUri(authPayload: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Handle invalid token state by either renewing it or un-authenticates
     * the user locally if the token cannot be refreshed
     */
    override fun onInvalidToken() {
        if (connectivityHelper.isConnected) {
            settings.authenticatedUserId = CrunchySettings.INVALID_USER_ID
            settings.isAuthenticated = false
            Timber.tag(moduleTag).e("Authentication token is null, application is logging user out!")
        }
    }

    /**
     * Handle invalid token state by either renewing it or un-authenticates
     * the user locally if the token cannot be refreshed
     */
    suspend fun onInvalidToken(
        userDao: CrunchyUserDao,
        loginDao: CrunchyLoginDao,
        sessionCoreDao: CrunchySessionCoreDao
    ) {
        withContext(Dispatchers.Main) { onInvalidToken() }

        userDao.clearTable()
        loginDao.clearTable()
        sessionDao.clearTable()
        sessionCoreDao.clearTable()
    }

    companion object {
        private const val ACCESS_TOKEN = "access_token"
        private const val DEVICE_TYPE = "device_type"
        private const val DEVICE_ID = "device_id"
        private const val SESSION_ID = "session_id"
        private const val LOCALE = "locale"
    }
}