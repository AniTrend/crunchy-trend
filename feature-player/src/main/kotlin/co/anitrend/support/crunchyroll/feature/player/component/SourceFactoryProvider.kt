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

import co.anitrend.arch.extension.lifecycle.SupportLifecycle
import com.devbrackets.android.exomedia.ExoMedia
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

class SourceFactoryProvider(cache: File?) : ExoMedia.DataSourceFactoryProvider, SupportLifecycle {

    // Adds a cache around the upstreamFactory
    private val simpleCache = SimpleCache(
        File(cache, "exo_video_cache"),
        LeastRecentlyUsedCacheEvictor(
            // Setting maximum storage cache to 2GB
            1024L * 1024L * 2048L
        )
    )

    override val moduleTag = SourceFactoryProvider::class.java.simpleName

    private fun releaseResources() {
        runCatching {
            simpleCache.release()
        }.exceptionOrNull()?.also {
            Timber.tag(moduleTag).e(it)
        }
    }

    override fun provide(userAgent: String, listener: TransferListener?): DataSource.Factory {
        val okHtpClient = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        // Updates the network data source to use the OKHttp implementation
        val upstreamFactory = OkHttpDataSourceFactory(okHtpClient, userAgent, listener)

        return CacheDataSourceFactory(
            simpleCache,
            upstreamFactory,
            CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
        )
    }

    /**
     * Triggered when the [androidx.lifecycle.LifecycleOwner] reaches it's onPause state
     */
    override fun onPause() {
        super.onPause()
        releaseResources()
    }
}