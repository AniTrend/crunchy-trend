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

package co.anitrend.support.crunchyroll.feature.search.koin

import co.anitrend.support.crunchyroll.feature.search.presenter.SeriesPresenter
import co.anitrend.support.crunchyroll.feature.search.ui.activity.SearchScreen
import co.anitrend.support.crunchyroll.feature.search.ui.fragment.SearchContentScreen
import co.anitrend.support.crunchyroll.feature.search.viewmodel.SeriesSearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

private val fragmentModule = module {
    scope<SearchScreen> {
        fragment {
            SearchContentScreen()
        }
    }
}

private val presenterModule = module {
    factory {
        SeriesPresenter(
            settings = get(),
            context = androidContext()
        )
    }
}

private val viewModelModule = module {
    viewModel {
        SeriesSearchViewModel(
            useCase = get()
        )
    }
}

private val featureModules = listOf(fragmentModule, presenterModule, viewModelModule)

private val koinModules by lazy {
    loadKoinModules(featureModules)
}

fun injectFeatureModules() = koinModules