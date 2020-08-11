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

package co.anitrend.support.crunchyroll.feature.media.koin

import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper
import co.anitrend.support.crunchyroll.feature.media.FeatureProvider
import co.anitrend.support.crunchyroll.feature.media.presenter.MediaPresenter
import co.anitrend.support.crunchyroll.feature.media.ui.activity.MediaScreen
import co.anitrend.support.crunchyroll.feature.media.ui.fragment.MediaContent
import co.anitrend.support.crunchyroll.feature.media.viewmodel.MediaViewModel
import co.anitrend.support.crunchyroll.navigation.Media
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val fragmentModule = module {
    scope<MediaScreen> {
        fragment {
            MediaContent()
        }
    }
}

private val presenterModule = module {
    factory {
        MediaPresenter(
            settings = get(),
            context = androidContext()
        )
    }
}

private val viewModelModule = module {
    viewModel {
        MediaViewModel(
            useCase = get()
        )
    }
}

private val featureModule = module {
    factory<Media.Provider> {
        FeatureProvider()
    }
}

val moduleHelper by lazy {
    DynamicFeatureModuleHelper(
        listOf(fragmentModule, presenterModule, viewModelModule, featureModule)
    )
}