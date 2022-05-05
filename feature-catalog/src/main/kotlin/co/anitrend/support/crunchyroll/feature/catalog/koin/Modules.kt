/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.feature.catalog.koin

import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper
import co.anitrend.support.crunchyroll.feature.catalog.FeatureProvider
import co.anitrend.support.crunchyroll.feature.catalog.presenter.CatalogPresenter
import co.anitrend.support.crunchyroll.feature.catalog.ui.fragment.CatalogContent
import co.anitrend.support.crunchyroll.feature.catalog.viewmodel.CatalogViewModel
import co.anitrend.support.crunchyroll.navigation.Catalog
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val fragmentModule = module {
    fragment {
        CatalogContent()
    }
}

private val presenterModule = module {
    factory {
        CatalogPresenter(
            context = androidContext(),
            settings = get()
        )
    }
}

private val viewModelModule = module {
    viewModel {
        CatalogViewModel(
            catalogUseCase = get()
        )
    }
}

private val featureModule = module {
    factory<Catalog.Provider> {
        FeatureProvider()
    }
}

val moduleHelper by lazy {
    DynamicFeatureModuleHelper(
        listOf(fragmentModule, presenterModule, viewModelModule, featureModule)
    )
}