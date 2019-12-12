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

package co.anitrend.support.crunchyroll.data.session.koin


import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.session.datasource.remote.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.session.mapper.CoreSessionResponseMapper
import co.anitrend.support.crunchyroll.data.session.mapper.SessionResponseMapper
import co.anitrend.support.crunchyroll.data.session.repository.SessionRepository
import co.anitrend.support.crunchyroll.data.session.source.CoreSessionSourceImpl
import co.anitrend.support.crunchyroll.data.session.source.NormalSessionSourceImpl
import co.anitrend.support.crunchyroll.data.session.source.UnblockSessionSourceImpl
import co.anitrend.support.crunchyroll.data.session.usecase.CoreSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.session.usecase.NormalSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.session.usecase.UnblockSessionUseCaseImpl
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        CoreSessionSourceImpl(
            dao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            endpoint = CrunchySessionEndpoint.create(),
            mapper = get(),
            settings = get(),
            supportDispatchers = get(),
            supportConnectivity = get()
        )
    }
    factory {
        NormalSessionSourceImpl(
            endpoint = CrunchyAuthenticationEndpoint.create(),
            dao = get<CrunchyDatabase>().crunchySessionDao(),
            coreSessionDao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            loginDao = get<CrunchyDatabase>().crunchyLoginDao(),
            mapper = get(),
            settings = get(),
            supportDispatchers = get(),
            supportConnectivity = get()
        )
    }
    factory {
        UnblockSessionSourceImpl(
            dao = get<CrunchyDatabase>().crunchySessionDao(),
            endpoint = CrunchySessionEndpoint.create(),
            coreSessionDao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            loginDao = get<CrunchyDatabase>().crunchyLoginDao(),
            mapper = get(),
            settings = get(),
            supportDispatchers = get(),
            supportConnectivity = get()
        )
    }
}

private val mapperModule = module {
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
}

private val repositoryModule = module {
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

val sessionModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule
)