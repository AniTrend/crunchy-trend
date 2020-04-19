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

package co.anitrend.support.crunchyroll.data.api.helper

import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.model.TimeSpecification
import okhttp3.CacheControl
import okhttp3.Request
import timber.log.Timber

internal object CacheHelper {
    internal const val MAX_CACHE_SIZE = (1024 * 1024 * 25).toLong()

    private val moduleTag = CacheHelper::class.java.simpleName

    /**
     * If we have internet connectivity, get the cache that was stored to the equivalent [cacheAge].
     * If the cache is older, then discard it, and indicate an error in fetching the response.
     *
     * [cacheAge] is responsible for this behavior.
     *
     * Otherwise get the cache that was stored to the equivalent [staleAge], and if the cache
     * is older then discard it and indicate an error in fetching the response.
     *
     * [staleAge] is responsible for this behavior.
     *
     * @param connectivity connectivity helper
     * @param cacheAge duration cache is valid for
     * @param staleAge duration offline cache is valid for
     * @param request request to build on
     */
    internal fun addCacheControl(
        connectivity: SupportConnectivity,
        cacheAge: TimeSpecification,
        staleAge: TimeSpecification,
        request: Request
    ): Request.Builder {
        val host = request.url.host
        return if (connectivity.isConnected) {
            Timber.tag(moduleTag).v(
                "Online cache control applied on request to host: $host"
            )
            // "public, max-age=MAX_CACHE_AGE"
            request.newBuilder().cacheControl(
                CacheControl.Builder()
                    .maxAge(
                        cacheAge.time,
                        cacheAge.unit
                    ).build()
            )
        }
        else {
            Timber.tag(moduleTag).v(
                "Offline cache control applied on request to host: $host"
            )
            // "public, only-if-cached, max-stale=MAX_STALE_TIME"
            request.newBuilder().cacheControl(
                CacheControl.Builder()
                    .maxStale(
                        staleAge.time,
                        staleAge.unit
                    ).onlyIfCached()
                    .build()
            )
        }
    }
}