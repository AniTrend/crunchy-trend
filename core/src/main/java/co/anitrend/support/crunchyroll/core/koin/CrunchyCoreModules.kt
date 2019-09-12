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

import co.anitrend.arch.extension.util.contract.ISupportDateHelper
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.viewmodel.*
import co.anitrend.support.crunchyroll.data.usecase.authentication.LoginUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.authentication.LogoutUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.media.MediaStreamUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.rss.MediaListingUseCaseImpl
import co.anitrend.support.crunchyroll.data.usecase.rss.NewsUseCaseImpl
import co.anitrend.support.crunchyroll.data.util.CrunchyDateHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

private val coreModule = module {
    single<ISupportDateHelper> {
        CrunchyDateHelper()
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

val viewModelModule = module {
    viewModel {
        LoginViewModel(
            useCase = get<LoginUseCaseImpl>()
        )
    }
    viewModel {
        LogoutViewModel(
            useCase = get<LogoutUseCaseImpl>()
        )
    }
    viewModel {
        MediaListingViewModel(
            useCase = get<MediaListingUseCaseImpl>()
        )
    }
    viewModel {
        NewsViewModel(
            useCase = get<NewsUseCaseImpl>()
        )
    }
    viewModel {
        MediaStreamViewModel(
            useCase = get<MediaStreamUseCaseImpl>()
        )
    }
}

val coreModules = listOf(coreModule, presenterModule, viewModelModule)