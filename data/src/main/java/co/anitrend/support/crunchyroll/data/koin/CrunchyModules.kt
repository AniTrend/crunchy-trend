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
import co.anitrend.support.crunchyroll.data.api.endpoint.json.*
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyInterceptor
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthenticationHelper
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.repository.auth.CrunchyAuthRepository
import co.anitrend.support.crunchyroll.data.repository.session.CrunchySessionRepository
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import io.wax911.support.extension.util.SupportConnectivityHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val crunchyModules = module {
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

val crunchyNetworkModules = module {
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
            sessionWithAuthenticatedUser = get<CrunchyDatabase>().crunchySessionDao()
        )
    }
    single {
        CrunchyInterceptor(
            authenticationHelper = get()
        )
    }
}

val crunchyEndpointModules = module {
    factory {
        CrunchyAuthEndpoint.createService()
    }
    factory {
        CrunchyCollectionEndpoint.createService()
    }
    factory {
        CrunchyMediaEndpoint.createService()
    }
    factory {
        CrunchySeriesEndpoint.createService()
    }
    factory {
        CrunchySessionEndpoint.createService()
    }
}

val crunchyRepositoryModules = module {
    factory {
        CrunchyAuthRepository(
            authEndpoint = get()
        )
    }
    factory {
        CrunchySessionRepository(
            authEndpoint = get(),
            sessionEndpoint = get()
        )
    }
}