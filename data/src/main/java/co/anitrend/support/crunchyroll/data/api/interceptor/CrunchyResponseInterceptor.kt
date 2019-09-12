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

import co.anitrend.support.crunchyroll.data.api.converter.CrunchyConverterFactory
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthentication
import co.anitrend.support.crunchyroll.data.extension.composeWith
import co.anitrend.support.crunchyroll.data.extension.typeTokenOf
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

class CrunchyResponseInterceptor(
    private val authentication: CrunchyAuthentication
) : Interceptor {

    private val lock = Object()
    private val json by lazy {
        CrunchyConverterFactory.GSON_BUILDER
            .addDeserializationExclusionStrategy(
                object : ExclusionStrategy {
                    override fun shouldSkipClass(clazz: Class<*>?) = false

                    override fun shouldSkipField(f: FieldAttributes?) = f?.name == "data"
                }
        ).create()
    }

    private val retryCount: AtomicInteger = AtomicInteger(0)

    private fun convertResponse(body: String?) : CrunchyContainer<Any?>? {
        return json.fromJson(body, typeTokenOf<CrunchyContainer<Any?>?>())
    }

    private fun retryFailedRequest(request: Request): Request.Builder {
        return runBlocking(Dispatchers.IO) {
            authentication.refreshSession(request)
        }
    }

    private fun buildResponseBody(content: String?, mediaType: MediaType?): ResponseBody? {
        return content?.toResponseBody(mediaType)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var original = chain.request()
        val response = chain.proceed(original)

        val body = response.body?.string()
        val mimeType = response.body?.contentType()
        val currentResponse = convertResponse(body)

        val priorBody = response.priorResponse?.body?.string()
        val priorResponse = convertResponse(priorBody)

        if (currentResponse != null) {
            if (currentResponse.error) {
                if (priorResponse?.error == true)
                    retryCount.incrementAndGet()

                if (retryCount.get() < 3)
                    if (currentResponse.isBadSession())
                        synchronized(lock) {
                            original = retryFailedRequest(original).build()
                        }
                else
                    Timber.w("Attempted to renew session and retiring after 3 attempts")
            } else
                retryCount.set(0)

            return currentResponse.composeWith(
                response,
                buildResponseBody(body, mimeType)
            )
        }

        return response
    }
}