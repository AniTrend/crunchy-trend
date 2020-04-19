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

package co.anitrend.support.crunchyroll.data.api.interceptor

import co.anitrend.arch.extension.LAZY_MODE_SYNCHRONIZED
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.api.converter.CrunchyConverterFactory
import co.anitrend.support.crunchyroll.data.api.helper.ResponseHelper
import co.anitrend.support.crunchyroll.data.arch.extension.isCached
import co.anitrend.support.crunchyroll.data.authentication.helper.CrunchyAuthenticationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.*
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Intercepts responses changing them if and when needed
 */
internal class CrunchyResponseInterceptor(
    private val connectivity: SupportConnectivity,
    private val authentication: CrunchyAuthenticationHelper,
    private val dispatchers: SupportDispatchers
) : Interceptor {

    private val responseHelper by lazy(LAZY_MODE_SYNCHRONIZED) {
        ResponseHelper(
            json = CrunchyConverterFactory.GSON_BUILDER.create()
        )
    }

    private val moduleTag = CrunchyResponseInterceptor::class.java.simpleName

    private val retryCount = AtomicInteger(0)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val response = chain.proceed(original)

        val crunchyResponse = responseHelper.reconstrctResponseUsing(response)

        if (crunchyResponse?.code == 401) {
            if (!crunchyResponse.isCached()) {
                val request = authenticate(crunchyResponse)
                if (request != null)
                    return chain.proceed(request)
            } else
                Timber.tag(moduleTag).w(
                    "Cached network response detected, re-authentication has been skipped"
                )
        }

        return crunchyResponse ?: response
    }


    /**
     * Returns a request that includes a credential to satisfy an authentication challenge in
     * [response]. Returns null if the challenge cannot be satisfied.
     *
     * The route is best effort, it currently may not always be provided even when logically
     * available. It may also not be provided when an authenticator is re-used manually in an
     * application interceptor, such as when implementing client-specific retries.
     */
    private fun authenticate(response: Response): Request? {
        val origin = response.request
        Timber.tag(moduleTag).v(
            "Attempting to authenticate request on for host: ${origin.url.host}"
        )
        if (connectivity.isConnected) {
            if (response.priorResponse?.isSuccessful != true) {
                val retries = retryCount.incrementAndGet()
                if (retries >= 3) {
                    val waitDuration = (retries * retries * 100).toLong()
                    runBlocking(dispatchers.io) {
                        delay(waitDuration)
                        authentication.invalidateSession()
                    }
                    retryCount.set(0)
                }
            }

            return runBlocking(dispatchers.io) {
                authentication.refreshSession(origin).build()
            }
        }

        Timber.tag(moduleTag).d(
            "Device is currently offline, skipping authentication process"
        )
        return null
    }
}