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

package co.anitrend.support.crunchyroll.feature.player.component

import com.devbrackets.android.exomedia.ExoMedia
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import okhttp3.OkHttpClient
import timber.log.Timber
import java.util.concurrent.TimeUnit

class SourceFactoryProvider(
    private val cache: Cache
) : ExoMedia.DataSourceFactoryProvider {

    private var cacheFactory: CacheDataSource.Factory? = null

    private val moduleTag = SourceFactoryProvider::class.java.simpleName

    override fun provide(userAgent: String, listener: TransferListener?): DataSource.Factory {
        if (cacheFactory == null) {
            val okHtpClient = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

            // Updates the network data source to use the OKHttp implementation
            val upstreamFactory = OkHttpDataSourceFactory(okHtpClient, userAgent, listener)
            Timber.tag(moduleTag).d(
                "Initializing cache factory backed by okhttp client. $userAgent"
            )

            cacheFactory = CacheDataSource.Factory()
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                .setCache(cache)
        }

        return cacheFactory!!
    }
}