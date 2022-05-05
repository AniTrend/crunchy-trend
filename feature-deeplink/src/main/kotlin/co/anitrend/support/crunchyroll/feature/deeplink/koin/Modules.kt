/*
 *    Copyright 2021 AniTrend
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

package co.anitrend.support.crunchyroll.feature.deeplink.koin

import android.content.Intent
import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.feature.deeplink.environment.AppEnvironment
import co.anitrend.support.crunchyroll.feature.deeplink.routes.FallbackRoute
import co.anitrend.support.crunchyroll.feature.deeplink.routes.MainRoute
import co.anitrend.support.crunchyroll.feature.deeplink.routes.PlayerRoute
import co.anitrend.support.crunchyroll.feature.deeplink.routes.SeriesRoute
import com.hellofresh.deeplink.DeepLinkParser
import com.hellofresh.deeplink.Environment
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val coreModule = module {
    factory<Environment> {
        val settings = get<CrunchySettings>()
        AppEnvironment(
            context = androidContext(),
            isAuthenticated = settings.isAuthenticated.value
        )
    }
}

private val routerModule = module {
    single {
        DeepLinkParser.of<Intent?>(get())
            .addRoute(MainRoute)
            .addRoute(SeriesRoute)
            .addRoute(PlayerRoute)
            .addFallbackAction(FallbackRoute)
            .build()
    }
}

val moduleHelper = DynamicFeatureModuleHelper(
    listOf(coreModule, routerModule)
)