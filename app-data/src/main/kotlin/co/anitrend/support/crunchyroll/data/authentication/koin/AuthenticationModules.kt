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

package co.anitrend.support.crunchyroll.data.authentication.koin

import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.arch.extension.api
import co.anitrend.support.crunchyroll.data.arch.extension.db
import co.anitrend.support.crunchyroll.data.authentication.helper.CrunchyAuthenticationHelper
import co.anitrend.support.crunchyroll.data.authentication.mapper.LoginResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.mapper.LogoutResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.repository.AuthenticationRepository
import co.anitrend.support.crunchyroll.data.authentication.source.LoginSourceImpl
import co.anitrend.support.crunchyroll.data.authentication.source.LogoutSourceImpl
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LoginSource
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LogoutSource
import co.anitrend.support.crunchyroll.data.authentication.usecase.LoginUseCaseImpl
import co.anitrend.support.crunchyroll.data.authentication.usecase.LoginUseCaseType
import co.anitrend.support.crunchyroll.data.authentication.usecase.LogoutUseCaseImpl
import co.anitrend.support.crunchyroll.data.authentication.usecase.LogoutUseCaseType
import co.anitrend.support.crunchyroll.data.session.usecase.CoreSessionUseCaseImpl
import co.anitrend.support.crunchyroll.data.session.usecase.UnblockSessionUseCaseImpl
import org.koin.dsl.bind
import org.koin.dsl.module

private val coreModule = module {
    single {
        CrunchyAuthenticationHelper(
            connectivityHelper = get(),
            settings = get(),
            unblockSessionUseCase = get<UnblockSessionUseCaseImpl>(),
            coreSessionUseCase = get<CoreSessionUseCaseImpl>(),
            sessionDao = db().crunchySessionDao(),
            sessionCoreDao = db().crunchySessionCoreDao(),
            sessionLocale = get()
        )
    }
}

private val dataSourceModule = module {
    factory {
        LoginSourceImpl(
            dao = db().crunchyLoginDao(),
            endpoint = api(EndpointType.AUTH),
            mapper = get(),
            settings = get(),
            supportDispatchers = get(),
            supportConnectivity = get(),
            sessionRepository = get(),
            authenticationHelper = get()
        )
    } bind LoginSource::class
    factory {
        LogoutSourceImpl(
            sessionCoreDao = db().crunchySessionCoreDao(),
            sessionDao = db().crunchySessionDao(),
            dao = db().crunchyLoginDao(),
            endpoint = api(EndpointType.AUTH),
            mapper = get(),
            settings = get(),
            supportDispatchers = get(),
            sessionRepository = get()
        )
    } bind LogoutSource::class
}

private val mapperModule = module {
    factory {
        LoginResponseMapper(
            dao = db().crunchyLoginDao()
        )
    }
    factory {
        LogoutResponseMapper()
    }
}

private val repositoryModule = module {
    factory {
        AuthenticationRepository(
            loginSource = get(),
            logoutSource = get()
        )
    }
}

private val useCaseModule = module {
    factory<LoginUseCaseType> {
        LoginUseCaseImpl(
            repository = get()
        )
    }
    factory<LogoutUseCaseType> {
        LogoutUseCaseImpl(
            repository = get()
        )
    }
}

val authenticationModules = listOf(
    coreModule, dataSourceModule, mapperModule, repositoryModule, useCaseModule
)