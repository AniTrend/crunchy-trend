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
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.session.datasource.remote.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyRequestInterceptor
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyResponseInterceptor
import co.anitrend.support.crunchyroll.data.authentication.helper.CrunchyAuthentication
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.authentication.source.LoginSourceImpl
import co.anitrend.support.crunchyroll.data.authentication.source.LogoutSourceImpl
import co.anitrend.support.crunchyroll.data.stream.source.CrunchyStreamSourceImpl
import co.anitrend.support.crunchyroll.data.episode.source.EpisodeFeedSourceImpl
import co.anitrend.support.crunchyroll.data.news.source.NewsSourceImpl
import co.anitrend.support.crunchyroll.data.session.source.CoreSessionSourceImpl
import co.anitrend.support.crunchyroll.data.session.source.NormalSessionSourceImpl
import co.anitrend.support.crunchyroll.data.session.source.UnblockSessionSourceImpl
import co.anitrend.support.crunchyroll.data.authentication.mapper.LoginResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.mapper.LogoutResponseMapper
import co.anitrend.support.crunchyroll.data.stream.mapper.CrunchyStreamResponseMapper
import co.anitrend.support.crunchyroll.data.episode.mapper.EpisodeFeedResponseMapper
import co.anitrend.support.crunchyroll.data.news.mapper.NewsResponseMapper
import co.anitrend.support.crunchyroll.data.session.mapper.CoreSessionResponseMapper
import co.anitrend.support.crunchyroll.data.session.mapper.SessionResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.repository.AuthenticationRepository
import co.anitrend.support.crunchyroll.data.stream.repository.CrunchyStreamRepository
import co.anitrend.support.crunchyroll.data.episode.repository.EpisodeFeedRepository
import co.anitrend.support.crunchyroll.data.news.repository.NewsRepository
import co.anitrend.support.crunchyroll.data.session.repository.SessionRepository
import co.anitrend.support.crunchyroll.data.authentication.usecase.LoginUseCaseImpl
import co.anitrend.support.crunchyroll.data.authentication.usecase.LogoutUseCaseImpl
import co.anitrend.support.crunchyroll.data.episode.datasource.remote.CrunchyEpisodeFeedEndpoint
import co.anitrend.support.crunchyroll.data.news.datasource.remote.CrunchyNewsFeedEndpoint
import co.anitrend.support.crunchyroll.data.stream.usecase.CrunchyStreamUseCaseImpl
import co.anitrend.support.crunchyroll.data.episode.usecase.EpisodeFeedUseCaseImpl
import co.anitrend.support.crunchyroll.data.news.usecase.NewsUseCaseImpl
import co.anitrend.support.crunchyroll.data.session.usecase.CoreSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.session.usecase.NormalSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.session.usecase.UnblockSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.stream.datasource.remote.CrunchyStreamEndpoint
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
        EpisodeFeedResponseMapper(
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
        CrunchyStreamResponseMapper()
    }
}

private val sourceModule = module {
    factory {
        NewsSourceImpl(
            responseMapper = get(),
            endpoint = CrunchyNewsFeedEndpoint.create(),
            dao = get<CrunchyDatabase>().crunchyRssNewsDao()
        )
    }
    factory {
        EpisodeFeedSourceImpl(
            responseMapper = get(),
            endpoint = CrunchyEpisodeFeedEndpoint.create(),
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
        CrunchyStreamSourceImpl(
            endpoint = CrunchyStreamEndpoint.create(),
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
        EpisodeFeedRepository(
            source = get<EpisodeFeedSourceImpl>()
        )
    }
    factory {
        AuthenticationRepository(
            loginSource = get<LoginSourceImpl>(),
            logoutSource = get<LogoutSourceImpl>()
        )
    }
    factory {
        CrunchyStreamRepository(
            source = get<CrunchyStreamSourceImpl>()
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
        EpisodeFeedUseCaseImpl(
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
        CrunchyStreamUseCaseImpl(
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

val crunchDataModules = listOf(
    dataModule, networkModule,
    useCaseModule, repositoryModule,
    mapperModule, sourceModule
)