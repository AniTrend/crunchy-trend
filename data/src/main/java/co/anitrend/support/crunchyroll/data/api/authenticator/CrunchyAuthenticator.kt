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

package co.anitrend.support.crunchyroll.data.api.authenticator

import co.anitrend.arch.extension.util.SupportConnectivityHelper
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthentication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import java.util.concurrent.atomic.AtomicInteger

class CrunchyAuthenticator(
    private val connectivityHelper: SupportConnectivityHelper,
    private val authentication: CrunchyAuthentication
) : Authenticator {

    private val retryCount: AtomicInteger = AtomicInteger(0)

    /**
     * Returns a request that includes a credential to satisfy an authentication challenge in
     * [response]. Returns null if the challenge cannot be satisfied.
     *
     * The route is best effort, it currently may not always be provided even when logically
     * available. It may also not be provided when an authenticator is re-used manually in an
     * application interceptor, such as when implementing client-specific retries.
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        val origin = response.request
        Timber.tag("CrunchyAuthenticator").d("Authenticator invoked!")
        if (connectivityHelper.isConnected) {
            if (response.priorResponse?.isSuccessful != true) {
                val retries = retryCount.incrementAndGet()
                if (retries >= 3) {
                    runBlocking(Dispatchers.IO) {
                        authentication.invalidateSession()
                    }
                    retryCount.set(0)
                }
            }

            return runBlocking(Dispatchers.IO) {
                authentication.refreshSession(origin).build()
            }
        }

        Timber.i("Device is currently offline, skipping interception")
        return null
    }
}