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
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyRequestInterceptor
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyResponseInterceptor
import co.anitrend.support.crunchyroll.data.authentication.koin.authenticationModules
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.episode.koin.episodeModules
import co.anitrend.support.crunchyroll.data.news.koin.newsModules
import co.anitrend.support.crunchyroll.data.series.koin.seriesModules
import co.anitrend.support.crunchyroll.data.session.koin.sessionModules
import co.anitrend.support.crunchyroll.data.stream.koin.streamModules
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

val crunchDataModules = listOf(
    dataModule, networkModule
) + authenticationModules +
        newsModules +
        episodeModules +
        streamModules +
        sessionModules +
        seriesModules