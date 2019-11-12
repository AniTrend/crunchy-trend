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
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyFeedEndpoint
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyRequestInterceptor
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyResponseInterceptor
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthentication
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.datasource.auto.authentication.LoginSourceImpl
import co.anitrend.support.crunchyroll.data.datasource.auto.authentication.LogoutSourceImpl
import co.anitrend.support.crunchyroll.data.datasource.auto.media.MediaStreamSourceImpl
import co.anitrend.support.crunchyroll.data.datasource.auto.rss.MediaListingSourceImpl
import co.anitrend.support.crunchyroll.data.datasource.auto.rss.NewsSourceImpl
import co.anitrend.support.crunchyroll.data.datasource.auto.session.CoreSessionSourceImpl
import co.anitrend.support.crunchyroll.data.datasource.auto.session.NormalSessionSourceImpl
import co.anitrend.support.crunchyroll.data.datasource.auto.session.UnblockSessionSourceImpl
import co.anitrend.support.crunchyroll.data.mapper.authentication.LoginResponseMapper
import co.anitrend.support.crunchyroll.data.mapper.authentication.LogoutResponseMapper
import co.anitrend.support.crunchyroll.data.mapper.media.MediaStreamResponseMapper
import co.anitrend.support.crunchyroll.data.mapper.rss.MediaListingResponseMapper
import co.anitrend.support.crunchyroll.data.mapper.rss.NewsResponseMapper
import co.anitrend.support.crunchyroll.data.mapper.session.CoreSessionResponseMapper
import co.anitrend.support.crunchyroll.data.mapper.session.SessionResponseMapper
import co.anitrend.support.crunchyroll.data.repository.authentication.AuthenticationRepository
import co.anitrend.support.crunchyroll.data.repository.media.MediaStreamRepository
import co.anitrend.support.crunchyroll.data.repository.rss.MediaListingRepository
import co.anitrend.support.crunchyroll.data.repository.rss.NewsRepository
import co.anitrend.support.crunchyroll.data.repository.session.SessionRepository
import co.anitrend.support.crunchyroll.data.usecase.authentication.LoginUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.authentication.LogoutUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.media.MediaStreamUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.rss.MediaListingUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.rss.NewsUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.session.CoreSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.session.NormalSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.session.UnblockSessionUseCaseImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val dataModule = module {
    single {
        CrunchyDatabase.newInstance(
            context =  androidContext()
        )
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
    factory {
        CrunchyAuthentication(
            connectivityHelper = get(),
            settings = get(),
            unblockSessionUseCase = get<UnblockSessionUseCaseImpl>(),
            coreSessionUseCase = get<CoreSessionUseCaseImpl>(),
            sessionDao = get<CrunchyDatabase>().crunchySessionDao(),
            sessionCoreDao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            sessionLocale = get()
        )
    }
    single {
        CrunchyRequestInterceptor(
            authentication = get()
        )
    }
    single {
        CrunchyResponseInterceptor(
            authentication = get(),
            connectivityHelper = get()
        )
    }
}

private val mapperModule = module {
    factory {
        NewsResponseMapper(
            dao = get<CrunchyDatabase>().crunchyRssNewsDao()
        )
    }
    factory {
        MediaListingResponseMapper(
            dao = get<CrunchyDatabase>().crunchyRssMediaDao()
        )
    }
    factory {
        LoginResponseMapper(
            dao = get<CrunchyDatabase>().crunchyLoginDao()
        )
    }
    factory {
        CoreSessionResponseMapper(
            dao = get<CrunchyDatabase>().crunchySessionCoreDao()
        )
    }
    factory {
        SessionResponseMapper(
            dao = get<CrunchyDatabase>().crunchySessionDao()
        )
    }
    factory {
        LogoutResponseMapper()
    }
    factory {
        MediaStreamResponseMapper()
    }
}

private val sourceModule = module {
    factory {
        NewsSourceImpl(
            responseMapper = get(),
            endpoint = CrunchyFeedEndpoint.create(),
            dao = get<CrunchyDatabase>().crunchyRssNewsDao()
        )
    }
    factory {
        MediaListingSourceImpl(
            responseMapper = get(),
            endpoint = CrunchyFeedEndpoint.create(),
            dao = get<CrunchyDatabase>().crunchyRssMediaDao(),
            settings = get()
        )
    }
    factory {
        CoreSessionSourceImpl(
            dao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            endpoint = CrunchySessionEndpoint.create(),
            responseMapper = get(),
            settings = get()
        )
    }
    factory {
        NormalSessionSourceImpl(
            endpoint = CrunchyAuthEndpoint.create(),
            dao = get<CrunchyDatabase>().crunchySessionDao(),
            coreSessionDao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            loginDao = get<CrunchyDatabase>().crunchyLoginDao(),
            responseMapper = get(),
            settings = get()
        )
    }
    factory {
        UnblockSessionSourceImpl(
            dao = get<CrunchyDatabase>().crunchySessionDao(),
            endpoint = CrunchySessionEndpoint.create(),
            coreSessionDao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            loginDao = get<CrunchyDatabase>().crunchyLoginDao(),
            responseMapper = get(),
            settings = get()
        )
    }
    factory {
        MediaStreamSourceImpl(
            endpoint = CrunchyMediaEndpoint.create(),
            responseMapper = get()
        )
    }
    factory {
        LoginSourceImpl(
            dao = get<CrunchyDatabase>().crunchyLoginDao(),
            endpoint = CrunchyAuthEndpoint.create(),
            responseMapper = get(),
            settings = get()
        )
    }
    factory {
        LogoutSourceImpl(
            sessionCoreDao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            sessionDao = get<CrunchyDatabase>().crunchySessionDao(),
            dao = get<CrunchyDatabase>().crunchyLoginDao(),
            endpoint = CrunchyAuthEndpoint.create(),
            responseMapper = get(),
            settings = get()
        )
    }
}

private val repositoryModule = module {
    factory {
        NewsRepository(
            source = get<NewsSourceImpl>()
        )
    }
    factory {
        MediaListingRepository(
            source = get<MediaListingSourceImpl>()
        )
    }
    factory {
        AuthenticationRepository(
            loginSource = get<LoginSourceImpl>(),
            logoutSource = get<LogoutSourceImpl>()
        )
    }
    factory {
        MediaStreamRepository(
            source = get<MediaStreamSourceImpl>()
        )
    }
    factory {
        SessionRepository(
            coreSource = get<CoreSessionSourceImpl>(),
            normalSource = get<NormalSessionSourceImpl>(),
            unblockedSource = get<UnblockSessionSourceImpl>()
        )
    }
}

private val useCaseModule = module {
    factory {
        NewsUseCaseImpl(
            repository = get()
        )
    }
    factory {
        MediaListingUseCaseImpl(
            repository = get()
        )
    }
    factory {
        LoginUseCaseImpl(
            repository = get()
        )
    }
    factory {
        LogoutUseCaseImpl(
            repository = get()
        )
    }
    factory {
        MediaStreamUseCaseImpl(
            repository = get()
        )
    }
    factory {
        CoreSessionUseCaseImpl(
            repository = get<SessionRepository>()
        )
    }
    factory {
        NormalSessionUseCaseImpl(
            repository = get<SessionRepository>()
        )
    }
    factory {
        UnblockSessionUseCaseImpl(
            repository = get<SessionRepository>()
        )
    }
}

val dataModules = listOf(
    dataModule, networkModule,
    useCaseModule, repositoryModule,
    mapperModule, sourceModule
)