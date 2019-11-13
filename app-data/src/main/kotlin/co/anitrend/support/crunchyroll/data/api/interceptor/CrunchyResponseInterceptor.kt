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

import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.api.converter.CrunchyConverterFactory
import co.anitrend.support.crunchyroll.data.authentication.helper.CrunchyAuthentication
import co.anitrend.support.crunchyroll.data.arch.extension.composeWith
import co.anitrend.support.crunchyroll.data.arch.extension.typeTokenOf
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

/**
 * Intercepts responses changing them if and when needed
 */
class CrunchyResponseInterceptor(
    private val connectivityHelper: SupportConnectivity,
    private val authentication: CrunchyAuthentication
) : Interceptor {

    private val json by lazy {
        CrunchyConverterFactory.GSON_BUILDER.create()
    }

    private val retryCount: AtomicInteger = AtomicInteger(0)

    private fun convertResponse(body: String?) : CrunchyContainer<Any?>? {
        return json.fromJson(body, typeTokenOf<CrunchyContainer<Any?>?>())
    }

    private fun buildResponseBody(content: String?, mediaType: MediaType?): ResponseBody? {
        return content?.toResponseBody(mediaType)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val response = chain.proceed(original)

        val body = response.body?.string()
        val mimeType = response.body?.contentType()
        val currentResponse = convertResponse(body)

        val newResponse = currentResponse?.composeWith(
            responseBody = buildResponseBody(
                body,
                mimeType
            ),
            response = response
        )

        if (newResponse?.code == 401) {
            val request = authenticate(newResponse)
            if (request != null)
                return chain.proceed(request)
        }

        if (newResponse != null)
            return newResponse

        return response
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