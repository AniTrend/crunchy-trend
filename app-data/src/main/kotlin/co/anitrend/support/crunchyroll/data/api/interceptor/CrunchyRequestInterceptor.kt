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

import co.anitrend.support.crunchyroll.data.authentication.helper.CrunchyAuthentication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * Authentication interceptor adds query parameters dynamically when the application is authenticated.
 * The context in which an [Interceptor] may be  parallel or asynchronous depending
 * on the dispatching caller, as such take care to assure thread safety
 */
class CrunchyRequestInterceptor(
    private val authentication: CrunchyAuthentication
) : Interceptor {

    private val regex = Regex("start_session|login|logout")

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val requestBuilder = runBlocking(Dispatchers.IO) {
            if (!original.url.pathSegments.last().contains(regex)) {
                Timber.d("Injecting query parameters with client interceptor")
                authentication.injectQueryParameters(original)
            }
            else {
                Timber.d("Skipping client interceptor for session endpoints")
                original.newBuilder()
            }
        }
        return chain.proceed(requestBuilder.build())
    }

}
