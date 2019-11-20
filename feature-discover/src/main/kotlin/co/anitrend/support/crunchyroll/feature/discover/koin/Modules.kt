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

package co.anitrend.support.crunchyroll.feature.discover.koin

import co.anitrend.support.crunchyroll.feature.discover.presenter.SeriesPresenter
import co.anitrend.support.crunchyroll.feature.discover.viewmodel.SeriesDiscoverViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

private val presenterModule = module {
    factory {
        SeriesPresenter(
            context = androidContext(),
            settings = get()
        )
    }
}

private val viewModelModule = module {
    viewModel {
        SeriesDiscoverViewModel(
            useCase = get()
        )
    }
}

private val featureModules = listOf(presenterModule, viewModelModule)

private val koinModules by lazy {
    loadKoinModules(featureModules)
}

fun injectFeatureModules() = koinModules