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

package co.anitrend.support.crunchyroll.feature.player.koin

import co.anitrend.support.crunchyroll.feature.player.presenter.StreamPresenter
import co.anitrend.support.crunchyroll.feature.player.viewmodel.MediaStreamViewModel
import com.devbrackets.android.exomedia.ExoMedia
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.io.File

private val coreModule = module {
    single {
        ExoMedia.DataSourceFactoryProvider { userAgent, listener ->
            // Updates the network data source to use the OKHttp implementation
            val upstreamFactory = OkHttpDataSourceFactory(OkHttpClient(), userAgent, listener)

            // Adds a cache around the upstreamFactory
            val simpleCache = SimpleCache(
                File(androidContext().externalCacheDir, "video_manager_disk_cache"),
                LeastRecentlyUsedCacheEvictor((256 * 1024 * 1024).toLong())
            )

            CacheDataSourceFactory(
                simpleCache,
                upstreamFactory,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
            )
        }
    }
}

private val presenterModule = module {
    factory {
        StreamPresenter(
            context = androidApplication(),
            settings = get()
        )
    }
}

private val viewModelModule = module {
    viewModel {
        MediaStreamViewModel(
            useCase = get()
        )
    }
}

private val featureModules = listOf(
    coreModule,
    presenterModule,
    viewModelModule
)

private val koinModules by lazy {
    loadKoinModules(featureModules)
}

fun injectFeatureModules() = koinModules