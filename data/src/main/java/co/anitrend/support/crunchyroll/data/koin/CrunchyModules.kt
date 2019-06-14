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
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyFeedEndpoint
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyInterceptor
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthenticationHelper
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.repository.auth.CrunchyAuthRepository
import co.anitrend.support.crunchyroll.data.repository.media.CrunchyMediaRepository
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
            sessionDao = get<CrunchyDatabase>().crunchySessionDao()
        )
    }
    single {
        CrunchyInterceptor(
            authenticationHelper = get()
        )
    }
}

val crunchyRepositoryModules = module {
    factory {
        CrunchyAuthRepository(
            authEndpoint = CrunchyAuthEndpoint.createService(),
            userDao = get<CrunchyDatabase>().crunchyUserDao(),
            loginDao = get<CrunchyDatabase>().crunchyLoginDao(),
            sessionCoreDao = get<CrunchyDatabase>().crunchySessionCoreDao()
        )
    }
    factory {
        CrunchySessionRepository(
            authEndpoint = CrunchyAuthEndpoint.createService(),
            sessionEndpoint = CrunchySessionEndpoint.createService(),
            sessionCoreDao = get<CrunchyDatabase>().crunchySessionCoreDao(),
            loginDao = get<CrunchyDatabase>().crunchyLoginDao(),
            userDao = get<CrunchyDatabase>().crunchyUserDao(),
            sessionDao = get<CrunchyDatabase>().crunchySessionDao()
        )
    }
    factory {
        CrunchyMediaRepository(
            mediaEndpoint = CrunchyMediaEndpoint.createService(),
            mediaDao = get<CrunchyDatabase>().crunchyMediaDao()
        )
    }
}