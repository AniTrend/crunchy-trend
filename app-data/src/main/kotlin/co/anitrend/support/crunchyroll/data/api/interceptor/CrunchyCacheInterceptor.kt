/*
 *    Copyright 2020 AniTrend
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
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

internal class CrunchyCacheInterceptor(
    private val connectivity: SupportConnectivity
) : Interceptor {

    /**
     * If we have internet connectivity, get the cache that was stored 5 minutes ago.
     * If the cache is older than [MAX_CACHE_AGE], then discard it, and indicate an
     * error in fetching the response. 'max-age' attribute is responsible for this behavior.
     *
     * Otherwise get the cache that was stored [MAX_STALE_TIME] ago, and if the cache is older than
     * [MAX_STALE_TIME] then discard it and indicate an error in fetching the response.
     * The 'max-stale' attribute is responsible for this behavior.
     * The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = if (connectivity.isConnected) {
            original.newBuilder().header(
                "Cache-Control",
                "public, max-age=$MAX_CACHE_AGE"
            )
        }
        else {
            original.newBuilder().header(
                "Cache-Control",
                "public, only-if-cached, max-stale=$MAX_STALE_TIME"
            )
        }
        return chain.proceed(request.build())
    }

    companion object {
        private val MAX_STALE_TIME = TimeUnit.DAYS.toSeconds(5)
        private val MAX_CACHE_AGE = TimeUnit.HOURS.toSeconds(1)
        internal const val MAX_CACHE_SIZE = (1024 * 1024 * 25).toLong()
    }
}