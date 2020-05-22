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


import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.arch.extension.api
import co.anitrend.support.crunchyroll.data.arch.extension.db
import co.anitrend.support.crunchyroll.data.session.mapper.CoreSessionResponseMapper
import co.anitrend.support.crunchyroll.data.session.mapper.SessionResponseMapper
import co.anitrend.support.crunchyroll.data.session.repository.SessionRepository
import co.anitrend.support.crunchyroll.data.session.source.CoreSessionSourceImpl
import co.anitrend.support.crunchyroll.data.session.source.NormalSessionSourceImpl
import co.anitrend.support.crunchyroll.data.session.source.UnblockSessionSourceImpl
import co.anitrend.support.crunchyroll.data.session.usecase.CoreSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.session.usecase.NormalSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.session.usecase.UnblockSessionUseCaseImpl
import co.anitrend.support.crunchyroll.domain.session.interactors.CoreSessionUseCase
import co.anitrend.support.crunchyroll.domain.session.interactors.NormalSessionUseCase
import co.anitrend.support.crunchyroll.domain.session.interactors.UnblockSessionUseCase
import org.koin.dsl.bind
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        CoreSessionSourceImpl(
            dao = db().crunchySessionCoreDao(),
            endpoint = api(EndpointType.SESSION_JSON),
            proxyEndpoint = api(EndpointType.SESSION_CORE),
            //proxyEndpoint = api(EndpointType.SESSION_PROXY),
            mapper = get(),
            settings = get(),
            supportDispatchers = get(),
            supportConnectivity = get()
        )
    }
    factory {
        NormalSessionSourceImpl(
            endpoint = api(EndpointType.SESSION_JSON),
            dao = db().crunchySessionDao(),
            coreSessionDao = db().crunchySessionCoreDao(),
            loginDao = db().crunchyLoginDao(),
            mapper = get(),
            settings = get(),
            supportDispatchers = get(),
            supportConnectivity = get()
        )
    }
    factory {
        UnblockSessionSourceImpl(
            dao = db().crunchySessionDao(),
            endpoint = api(EndpointType.SESSION_CORE),
            coreSessionDao = db().crunchySessionCoreDao(),
            loginDao = db().crunchyLoginDao(),
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
            dao = db().crunchySessionCoreDao()
        )
    }
    factory {
        SessionResponseMapper(
            dao = db().crunchySessionDao()
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
    } bind CoreSessionUseCase::class
    factory {
        NormalSessionUseCaseImpl(
            repository = get<SessionRepository>()
        )
    } bind NormalSessionUseCase::class
    factory {
        UnblockSessionUseCaseImpl(
            repository = get<SessionRepository>()
        )
    } bind UnblockSessionUseCase::class
}

val sessionModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule
)