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

package co.anitrend.support.crunchyroll.core.koin

import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.isLowRamDevice
import co.anitrend.arch.extension.util.contract.ISupportDateHelper
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.core.util.config.ConfigurationUtil
import co.anitrend.support.crunchyroll.core.util.locale.LocaleUtil
import co.anitrend.support.crunchyroll.core.util.locale.SessionLocaleProviderHelper
import co.anitrend.support.crunchyroll.core.util.theme.ThemeUtil
import co.anitrend.support.crunchyroll.data.locale.helper.ICrunchySessionLocale
import co.anitrend.support.crunchyroll.data.util.CrunchyDateHelper
import coil.ImageLoader
import coil.util.CoilUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.binds
import org.koin.dsl.module
import java.io.File

private val coreModule = module {
    single<ISupportDateHelper> {
        CrunchyDateHelper(
            context = androidContext()
        )
    }
    single {
        SupportStateLayoutConfiguration(
            loadingDrawable = R.drawable.ic_launcher_foreground,
            errorDrawable = R.drawable.ic_support_empty_state,
            loadingMessage = R.string.label_text_loading,
            retryAction = R.string.label_text_action_retry
        )
    }
    factory {
        CrunchySettings(
            context = androidContext()
        )
    } binds(CrunchySettings.BINDINGS)
    factory {
        ConfigurationUtil(
            settings = get(),
            localeUtil = get(),
            themeUtil = get()
        )
    }
    single {
        SupportDispatchers()
    }
}

private val configurationModule = module {
    single {
        LocaleUtil(
            settings = get()
        )
    }
    single {
        ThemeUtil(
            settings = get()
        )
    }
    factory<ICrunchySessionLocale> {
        SessionLocaleProviderHelper(
            localeUtil = get()
        )
    }
    factory {
        ImageLoader(androidContext()) {
            availableMemoryPercentage(0.2)
            bitmapPoolPercentage(0.2)
            crossfade(360)
            allowRgb565(!androidContext().isLowRamDevice())
            allowHardware(true)
            okHttpClient {
                OkHttpClient.Builder().cache(
                    Cache(
                        File(
                            androidContext().externalCacheDir,
                            "coil_image_cache"
                        ).apply { mkdirs() },
                        1024 * 1024 * 256
                    )
                ).build()
            }
        }
    }
}

private val presenterModule = module {
    factory {
        CrunchyCorePresenter(
            androidContext(),
            settings = get()
        )
    }
}

val coreModules = listOf(coreModule, configurationModule, presenterModule)