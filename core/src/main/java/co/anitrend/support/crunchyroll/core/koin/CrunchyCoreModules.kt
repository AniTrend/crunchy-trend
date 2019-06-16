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

import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.util.CrunchyDateHelper
import co.anitrend.support.crunchyroll.core.viewmodel.auth.CrunchyAuthViewModel
import co.anitrend.support.crunchyroll.core.viewmodel.media.CrunchyMediaViewModel
import co.anitrend.support.crunchyroll.core.viewmodel.rss.CrunchyRssMediaViewModel
import co.anitrend.support.crunchyroll.data.repository.auth.CrunchyAuthRepository
import co.anitrend.support.crunchyroll.data.repository.media.CrunchyMediaRepository
import co.anitrend.support.crunchyroll.data.repository.rss.CrunchyRssMediaRepository
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val crunchyCoreModules = module {
    single {
        CrunchyDateHelper()
    }
}

val crunchyCorePresenterModules = module {
    factory {
        CrunchyCorePresenter(
            androidContext(),
            settings = get()
        )
    }
}


val crunchyCoreViewModelModules = module {
    viewModel {
        CrunchyAuthViewModel(
            repository = get<CrunchyAuthRepository>()
        )
    }
    viewModel {
        CrunchyMediaViewModel(
            repository = get<CrunchyMediaRepository>()
        )
    }
    viewModel {
        CrunchyRssMediaViewModel(
            repository = get<CrunchyRssMediaRepository>()
        )
    }
    viewModel {
        CrunchyRssMediaViewModel(
            repository = get<CrunchyRssMediaRepository>()
        )
    }
}