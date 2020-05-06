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

package co.anitrend.support.crunchyroll.feature.listing.koin

import co.anitrend.support.crunchyroll.feature.listing.presenter.ListingPresenter
import co.anitrend.support.crunchyroll.feature.listing.ui.fragment.MediaFeedContent
import co.anitrend.support.crunchyroll.feature.listing.viewmodel.MediaListingViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

private val fragmentModule = module {
    fragment {
        MediaFeedContent()
    }
}

private val presenterModule = module {
    factory {
        ListingPresenter(
            context = androidApplication(),
            settings = get()
        )
    }
}

private val viewModelModule = module {
    viewModel {
        MediaListingViewModel(
            useCase = get()
        )
    }
}

private val featureModules = listOf(fragmentModule, presenterModule, viewModelModule)

private val koinModules by lazy {
    loadKoinModules(featureModules)
}

fun injectFeatureModules() = koinModules