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
import co.anitrend.support.crunchyroll.data.util.CrunchyDateHelper
import co.anitrend.support.crunchyroll.core.viewmodel.auth.CrunchyAuthViewModel
import co.anitrend.support.crunchyroll.core.viewmodel.media.CrunchyMediaInfoViewModel
import co.anitrend.support.crunchyroll.core.viewmodel.media.CrunchyMediaStreamViewModel
import co.anitrend.support.crunchyroll.core.viewmodel.media.CrunchyMediaListViewModel
import co.anitrend.support.crunchyroll.core.viewmodel.rss.CrunchyRssMediaViewModel
import co.anitrend.support.crunchyroll.core.viewmodel.rss.CrunchyRssNewsViewModel
import co.anitrend.support.crunchyroll.data.repository.auth.CrunchyAuthRepository
import co.anitrend.support.crunchyroll.data.repository.media.CrunchyMediaInfoRepository
import co.anitrend.support.crunchyroll.data.repository.media.CrunchyMediaListRepository
import co.anitrend.support.crunchyroll.data.repository.media.CrunchyMediaStreamRepository
import co.anitrend.support.crunchyroll.data.repository.rss.CrunchyRssMediaRepository
import co.anitrend.support.crunchyroll.data.repository.rss.CrunchyRssNewsRepository
import io.wax911.support.extension.util.contract.ISupportDateHelper
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val crunchyCoreModules = module {
    single<ISupportDateHelper> {
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
        CrunchyMediaListViewModel(
            repository = get<CrunchyMediaListRepository>()
        )
    }
    viewModel {
        CrunchyMediaInfoViewModel(
            repository = get<CrunchyMediaInfoRepository>()
        )
    }
    viewModel {
        CrunchyMediaStreamViewModel(
            repository = get<CrunchyMediaStreamRepository>()
        )
    }
    viewModel {
        CrunchyRssMediaViewModel(
            repository = get<CrunchyRssMediaRepository>()
        )
    }
    viewModel {
        CrunchyRssNewsViewModel(
            repository = get<CrunchyRssNewsRepository>()
        )
    }
}