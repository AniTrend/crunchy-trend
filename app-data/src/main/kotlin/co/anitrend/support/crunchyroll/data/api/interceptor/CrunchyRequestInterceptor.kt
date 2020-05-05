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

import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.api.helper.CacheHelper
import co.anitrend.support.crunchyroll.data.arch.model.TimeSpecification
import co.anitrend.support.crunchyroll.data.authentication.helper.CrunchyAuthenticationHelper
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Authentication interceptor adds query parameters dynamically when the application is authenticated.
 * The context in which an [Interceptor] may be  parallel or asynchronous depending
 * on the dispatching caller, as such take care to assure thread safety
 */
internal class CrunchyRequestInterceptor(
    private val authentication: CrunchyAuthenticationHelper,
    private val connectivity: SupportConnectivity,
    private val dispatcher: SupportDispatchers
) : Interceptor {

    private val moduleTag =  CrunchyRequestInterceptor::class.java.simpleName

    private fun addDynamicParameters(request: Request): Request.Builder {
        Timber.tag(moduleTag).d("Injecting query parameters on host: ${request.url.host}")
        return runBlocking(dispatcher.confined) {
            authentication.injectQueryParameters(request)
        }
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = run {
            val dynamicRequest = addDynamicParameters(original).build()
            val cacheControlledRequest = CacheHelper.addCacheControl(
                connectivity = connectivity,
                cacheAge = TimeSpecification(15, TimeUnit.MINUTES),
                staleAge = TimeSpecification(3, TimeUnit.DAYS),
                request = dynamicRequest
            )
            cacheControlledRequest.build()
        }
        return chain.proceed(request)
    }
}
