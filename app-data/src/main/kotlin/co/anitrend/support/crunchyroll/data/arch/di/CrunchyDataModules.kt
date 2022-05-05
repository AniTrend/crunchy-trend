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

package co.anitrend.support.crunchyroll.data.arch.di

import android.accounts.AccountManager
import android.net.ConnectivityManager
import co.anitrend.arch.extension.ext.systemServiceOf
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.converter.CrunchyConverterFactory
import co.anitrend.support.crunchyroll.data.api.helper.CacheHelper
import co.anitrend.support.crunchyroll.data.arch.database.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.arch.database.common.ICrunchyDatabase
import co.anitrend.support.crunchyroll.data.authentication.koin.authenticationModules
import co.anitrend.support.crunchyroll.data.batch.koin.batchModules
import co.anitrend.support.crunchyroll.data.catalog.koin.catalogModules
import co.anitrend.support.crunchyroll.data.collection.koin.collectionModules
import co.anitrend.support.crunchyroll.data.episode.koin.episodeModules
import co.anitrend.support.crunchyroll.data.media.koin.mediaModules
import co.anitrend.support.crunchyroll.data.news.koin.newsModules
import co.anitrend.support.crunchyroll.data.series.koin.seriesModules
import co.anitrend.support.crunchyroll.data.session.koin.sessionModules
import co.anitrend.support.crunchyroll.data.stream.koin.streamModules
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private val dataModule = module {
    single<ICrunchyDatabase> {
        CrunchyDatabase.newInstance(
            context =  androidContext()
        )
    }
    factory {
        CrunchyConverterFactory.create()
    }
    single {
        AccountManager.get(androidApplication())
    }
}

private val networkModule = module {
    factory {
        SupportConnectivity(
            androidContext().systemServiceOf<ConnectivityManager>()
        )
    }
    factory { (interceptorLogLevel: HttpLoggingInterceptor.Level) ->
        val okHttpClientBuilder = OkHttpClient.Builder()
            .cache(
                Cache(
                    androidContext().cacheDir,
                    CacheHelper.MAX_CACHE_SIZE
                )
            )
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        when {
            BuildConfig.DEBUG -> {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = interceptorLogLevel
                okHttpClientBuilder
                    .addInterceptor(httpLoggingInterceptor)
                    .addInterceptor(
                        ChuckerInterceptor.Builder(context = androidContext())
                                .collector(
                                    collector = ChuckerCollector(
                                        context = androidContext(),
                                        // Toggles visibility of the push notification
                                        showNotification = true,
                                        // Allows to customize the retention period of collected data
                                        retentionPeriod = RetentionManager.Period.ONE_WEEK
                                    )
                                    // The max body content length in bytes, after this responses will be truncated.
                                )
                                .maxContentLength(
                                    length = 250000L
                                    // List of headers to replace with ** in the Chucker UI
                                )
                                .redactHeaders(
                                    headerNames = setOf("Auth-Token")
                                )
                                .alwaysReadResponseBody(false)
                                .build()
                    )
            }
        }

        okHttpClientBuilder
    }
    factory {
        Retrofit.Builder()
            .addConverterFactory(
                get<CrunchyConverterFactory>()
            )
    }
}

val crunchDataModules = listOf(
    dataModule, networkModule
) + authenticationModules +
        newsModules +
        episodeModules +
        streamModules +
        sessionModules +
        seriesModules +
        catalogModules +
        collectionModules +
        mediaModules +
        batchModules