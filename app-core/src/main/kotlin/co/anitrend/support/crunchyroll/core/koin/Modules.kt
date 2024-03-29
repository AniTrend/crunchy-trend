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

import android.webkit.WebView
import co.anitrend.arch.core.model.IStateLayoutConfig
import co.anitrend.arch.extension.dispatchers.SupportDispatcher
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.arch.extension.ext.isLowRamDevice
import co.anitrend.arch.extension.util.date.contract.AbstractSupportDateHelper
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.core.helper.StorageHelper
import co.anitrend.support.crunchyroll.core.model.UserAgent
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.core.util.config.ConfigurationUtil
import co.anitrend.support.crunchyroll.core.util.locale.LocaleUtil
import co.anitrend.support.crunchyroll.core.util.locale.SessionLocaleProviderHelper
import co.anitrend.support.crunchyroll.core.util.theme.ThemeUtil
import co.anitrend.support.crunchyroll.data.arch.di.crunchDataModules
import co.anitrend.support.crunchyroll.data.locale.helper.ICrunchySessionLocale
import co.anitrend.support.crunchyroll.data.util.CrunchyDateUtil
import coil.ImageLoader
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

private val coreModule = module {
    single {
        CrunchyDateUtil()
    } bind AbstractSupportDateHelper::class
    single {
        StateLayoutConfig(
            loadingDrawable = R.drawable.ic_launcher_foreground,
            errorDrawable = R.drawable.ic_support_empty_state,
            loadingMessage = R.string.label_text_loading,
            retryAction = R.string.label_text_action_retry
        )
    } bind IStateLayoutConfig::class
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
    single<ISupportDispatcher> {
        SupportDispatcher()
    }
    single {
        UserAgent(
            WebView(
                androidContext()
            ).settings.userAgentString
        )
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
        val isLowRamDevice = androidContext().isLowRamDevice()
        val memoryLimit = if (isLowRamDevice) 0.15 else 0.35
        val dispatcher = get<ISupportDispatcher>()
        val loader = ImageLoader.Builder(androidContext())
            .availableMemoryPercentage(memoryLimit)
            .bitmapPoolPercentage(memoryLimit)
            .dispatcher(dispatcher.io)
            .okHttpClient {
                OkHttpClient.Builder().cache(
                    Cache(
                        StorageHelper.getImageCache(
                            context = androidContext()
                        ),
                        StorageHelper.getStorageUsageLimit(
                            context = androidContext(),
                            settings = get()
                        )
                    )
                ).build()
            }

            if (!isLowRamDevice)
                loader.crossfade(360)
            else
                loader.allowRgb565(true)

        loader.build()
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

val coreModules = listOf(coreModule, configurationModule, presenterModule) + crunchDataModules