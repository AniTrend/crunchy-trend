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

import co.anitrend.support.crunchyroll.core.helper.StorageHelper
import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper
import co.anitrend.support.crunchyroll.core.model.UserAgent
import co.anitrend.support.crunchyroll.feature.player.FeatureProvider
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.component.SourceFactoryProvider
import co.anitrend.support.crunchyroll.feature.player.plugin.PlaylistManagerPluginImpl
import co.anitrend.support.crunchyroll.feature.player.presenter.StreamPresenter
import co.anitrend.support.crunchyroll.feature.player.ui.activity.MediaPlayerScreen
import co.anitrend.support.crunchyroll.feature.player.ui.fragment.MediaStreamContent
import co.anitrend.support.crunchyroll.feature.player.viewmodel.MediaStreamViewModel
import co.anitrend.support.crunchyroll.navigation.MediaPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val coreModule = module {
    single {
        val offlineCache = SimpleCache(
            StorageHelper.getVideoCache(
                context = androidContext()
            ),
            LeastRecentlyUsedCacheEvictor(
                StorageHelper.getStorageUsageLimit(
                    context = androidContext(),
                    settings = get()
                )
            ),
            get<ExoDatabaseProvider>()
        )
        SourceFactoryProvider(
            cache = offlineCache
        )
    }
    single {
        DownloadNotificationHelper(
            androidContext(),
            androidContext().getString(
                R.string.media_notification_channel
            )
        )
    }
    single {
        PlaylistManagerPluginImpl(
            application = androidApplication()
        )
    }
    single {
        ExoDatabaseProvider(androidApplication())
    }
    single {
        val downloadCache = SimpleCache(
            StorageHelper.getVideoOfflineCache(
                context = androidContext()
            ),
            NoOpCacheEvictor(),
            get<ExoDatabaseProvider>()
        )

        val dataSourceFactory = DefaultHttpDataSourceFactory(
            get<UserAgent>().identifier
        )

        DownloadManager(
            androidContext(),
            get<ExoDatabaseProvider>(),
            downloadCache,
            dataSourceFactory
        )
    }
}

private val fragmentModule = module {
    scope<MediaPlayerScreen> {
        fragment {
            MediaStreamContent(
                presenter = get(),
                playlistManager = get(),
                sourceFactoryProvider = get()
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

private val featureModule = module {
    factory<MediaPlayer.Provider> {
        FeatureProvider()
    }
}

val moduleHelper by lazy {
    DynamicFeatureModuleHelper(
        listOf(
            coreModule,
            fragmentModule,
            presenterModule,
            viewModelModule,
            featureModule
        )
    )
}