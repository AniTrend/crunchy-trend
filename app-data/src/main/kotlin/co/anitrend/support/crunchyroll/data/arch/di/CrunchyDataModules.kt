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

import android.content.Context
import android.net.ConnectivityManager
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.converter.CrunchyConverterFactory
import co.anitrend.support.crunchyroll.data.api.endpoint.contract.CrunchyEndpointFactory
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchySeriesFeedEndpoint
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyRequestInterceptor
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyResponseInterceptor
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.authentication.koin.authenticationModules
import co.anitrend.support.crunchyroll.data.collection.datasource.remote.CrunchyCollectionEndpoint
import co.anitrend.support.crunchyroll.data.collection.koin.collectionModules
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.episode.datasource.remote.CrunchyEpisodeFeedEndpoint
import co.anitrend.support.crunchyroll.data.episode.koin.episodeModules
import co.anitrend.support.crunchyroll.data.locale.datasource.remote.CrunchyLocaleEndpoint
import co.anitrend.support.crunchyroll.data.media.datasource.remote.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.media.koin.mediaModules
import co.anitrend.support.crunchyroll.data.news.datasource.remote.CrunchyNewsFeedEndpoint
import co.anitrend.support.crunchyroll.data.news.koin.newsModules
import co.anitrend.support.crunchyroll.data.series.datasource.remote.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.series.koin.seriesModules
import co.anitrend.support.crunchyroll.data.session.datasource.remote.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.session.koin.sessionModules
import co.anitrend.support.crunchyroll.data.stream.datasource.remote.CrunchyStreamEndpoint
import co.anitrend.support.crunchyroll.data.stream.koin.streamModules
import co.anitrend.support.crunchyroll.data.tracker.datasource.remote.CrunchyTrackingEndpoint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private val dataModule = module {
    single {
        CrunchyDatabase.newInstance(
            context =  androidContext()
        )
    }
    factory {
        CrunchyConverterFactory.create()
    }
}

private val networkModule = module {
    factory {
        SupportConnectivity(
            androidContext().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager?
        )
    }
    single {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        when {
            BuildConfig.DEBUG -> {
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
                okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
            }
        }

        okHttpClientBuilder
    }
    single(named("rssOnSlug")) {
        Retrofit.Builder().client(
            get<OkHttpClient.Builder>()
                .build()
        )
            .addConverterFactory(get<CrunchyConverterFactory>())
            .baseUrl(BuildConfig.crunchyUrl)
            .build()
    }
    single(named("rss")) {
        Retrofit.Builder().client(
            get<OkHttpClient.Builder>()
                .build()
        )
            .addConverterFactory(get<CrunchyConverterFactory>())
            .baseUrl(BuildConfig.apiFeed)
            .build()
    }
    single(named("jsonSession")) {
        Retrofit.Builder().client(
            get<OkHttpClient.Builder>()
                .build()
        )
            .addConverterFactory(get<CrunchyConverterFactory>())
            .baseUrl(BuildConfig.apiSessionUrl)
            .build()
    }
    single(named("json")) {
        Retrofit.Builder().client(
            get<OkHttpClient.Builder>()
                .addInterceptor(
                    CrunchyRequestInterceptor(
                        authentication = get()
                    )
                )
                .addInterceptor(
                    CrunchyResponseInterceptor(
                        authentication = get(),
                        connectivityHelper = get(),
                        dispatchers = get()
                    )
                )
                .build()
        )
            .addConverterFactory(get<CrunchyConverterFactory>())
            .baseUrl(BuildConfig.apiUrl)
            .build()
    }
    single(named("jsonAuth")) {
        Retrofit.Builder().client(
            get<OkHttpClient.Builder>()
                .build()
        )
            .addConverterFactory(get<CrunchyConverterFactory>())
            .baseUrl(BuildConfig.apiUrl)
            .build()
    }
}

private val endpointModule = module {
    single {
        CrunchyEndpointFactory(
            CrunchySeriesFeedEndpoint::class,
            get(named("rssOnSlug"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchyEpisodeFeedEndpoint::class,
            get(named("rss"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchyNewsFeedEndpoint::class,
            get(named("rss"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchyAuthenticationEndpoint::class,
            get(named("jsonAuth"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchySessionEndpoint::class,
            get(named("jsonSession"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchyLocaleEndpoint::class,
            get(named("json"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchyMediaEndpoint::class,
            get(named("json"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchySeriesEndpoint::class,
            get(named("json"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchyStreamEndpoint::class,
            get(named("json"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchyCollectionEndpoint::class,
            get(named("json"))
        ).create()
    }

    single {
        CrunchyEndpointFactory(
            CrunchyTrackingEndpoint::class,
            get(named("json"))
        ).create()
    }
}

private val localSourceModule = module {
    single {
        get<CrunchyDatabase>().crunchySeriesDao()
    }
    single {
        get<CrunchyDatabase>().crunchySessionCoreDao()
    }
    single {
        get<CrunchyDatabase>().crunchyCollectionDao()
    }
    single {
        get<CrunchyDatabase>().crunchyMediaDao()
    }
    single {
        get<CrunchyDatabase>().crunchyLoginDao()
    }
    single {
        get<CrunchyDatabase>().crunchySessionDao()
    }
    single {
        get<CrunchyDatabase>().crunchyRssNewsDao()
    }
    single {
        get<CrunchyDatabase>().crunchyRssMediaDao()
    }
}

val crunchDataModules = listOf(
    dataModule, networkModule, endpointModule, localSourceModule
) + authenticationModules +
        newsModules +
        episodeModules +
        streamModules +
        sessionModules +
        seriesModules +
        collectionModules +
        mediaModules