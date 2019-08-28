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

package co.anitrend.support.crunchyroll.data.koin

import android.content.Context
import android.net.ConnectivityManager
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyFeedEndpoint
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyInterceptor
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthenticationHelper
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.repository.auth.CrunchyAuthRepository
import co.anitrend.support.crunchyroll.data.repository.media.CrunchyMediaInfoRepository
import co.anitrend.support.crunchyroll.data.repository.media.CrunchyMediaListRepository
import co.anitrend.support.crunchyroll.data.repository.media.CrunchyMediaStreamRepository
import co.anitrend.support.crunchyroll.data.repository.rss.CrunchyRssMediaRepository
import co.anitrend.support.crunchyroll.data.repository.rss.CrunchyRssNewsRepository
import co.anitrend.support.crunchyroll.data.usecase.auth.CrunchyAuthenticationUseCase
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaInfoUseCase
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaListUseCase
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaStreamUseCase
import co.anitrend.support.crunchyroll.data.usecase.rss.CrunchyRssMediaUseCase
import co.anitrend.support.crunchyroll.data.usecase.rss.CrunchyRssNewsUseCase
import co.anitrend.support.crunchyroll.data.usecase.series.CrunchySeriesUseCase
import co.anitrend.support.crunchyroll.data.usecase.session.CrunchySessionUseCase
import co.anitrend.support.crunchyroll.data.util.CrunchyDateHelper
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import io.wax911.support.extension.util.SupportConnectivityHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val crunchyDataModules = module {
    single {
        CrunchyDatabase.newInstance(
            context =  androidContext()
        )
    }
    factory {
        CrunchySettings(
            context = androidContext()
        )
    }
}

val crunchyDataNetworkModules = module {
    factory {
        SupportConnectivityHelper(
            androidContext().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager?
        )
    }
    factory {
        CrunchyAuthenticationHelper(
            connectivityHelper = get(),
            settings = get(),
            deviceToken = BuildConfig.clientToken,
            sessionDao = get<CrunchyDatabase>().crunchySessionDao()
        )
    }
    single {
        CrunchyInterceptor(
            authenticationHelper = get()
        )
    }
}

val crunchyDataUseCaseModules = module {
    factory {
        CrunchyAuthenticationUseCase(
            sessionCoreDao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            sessionDao = get<CrunchyDatabase>().crunchySessionDao(),
            authEndpoint = CrunchyAuthEndpoint.create(),
            loginDao = get<CrunchyDatabase>().crunchyLoginDao()
        )
    }
    factory {
        CrunchyMediaInfoUseCase(
            mediaEndpoint = CrunchyMediaEndpoint.create(),
            mediaDao = get<CrunchyDatabase>().crunchyMediaDao()
        )
    }
    factory {
        CrunchyMediaListUseCase(
            mediaEndpoint = CrunchyMediaEndpoint.create(),
            mediaDao = get<CrunchyDatabase>().crunchyMediaDao()
        )
    }
    factory {
        CrunchyMediaStreamUseCase(
            mediaEndpoint = CrunchyMediaEndpoint.create()
        )
    }
    factory {
        CrunchySeriesUseCase(
            seriesEndpoint = CrunchySeriesEndpoint.create(),
            seriesDao = get<CrunchyDatabase>().crunchySeriesDao()
        )
    }
    factory {
        CrunchySessionUseCase(
            sessionEndpoint  = CrunchySessionEndpoint.create(),
            sessionCoreDao  = get<CrunchyDatabase>().crunchySessionCoreDao(),
            crunchyLoginDao  = get<CrunchyDatabase>().crunchyLoginDao(),
            authEndpoint  = CrunchyAuthEndpoint.create(),
            sessionDao  = get<CrunchyDatabase>().crunchySessionDao(),
            dateHelper = get()
        )
    }
    factory {
        CrunchyRssMediaUseCase(
            rssMediaDao  = get<CrunchyDatabase>().crunchyRssMediaDao(),
            rssCrunchyEndpoint  = CrunchyEndpoint.create(),
            rssFeedCrunchyEndpoint  = CrunchyFeedEndpoint.create()
        )
    }
    factory {
        CrunchyRssNewsUseCase(
            rssNewsDao = get<CrunchyDatabase>().crunchyRssNewsDao(),
            rssCrunchyEndpoint = CrunchyEndpoint.create()
        )
    }
}

val crunchyDataRepositoryModules = module {
    factory {
        CrunchyAuthRepository(
            authenticationUseCase = get()
        )
    }
    factory {
        CrunchyMediaListRepository(
            useCase = get()
        )
    }
    factory {
        CrunchyMediaInfoRepository(
            useCase = get()
        )
    }
    factory {
        CrunchyMediaStreamRepository(
            useCase = get()
        )
    }
    factory {
        CrunchyRssMediaRepository(
            useCase = get()
        )
    }
    factory {
        CrunchyRssNewsRepository(
            useCase = get()
        )
    }
}