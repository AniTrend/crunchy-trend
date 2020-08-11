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

package co.anitrend.support.crunchyroll.feature.authentication.koin

import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper
import co.anitrend.support.crunchyroll.feature.authentication.FeatureProvider
import co.anitrend.support.crunchyroll.feature.authentication.presenter.AuthPresenter
import co.anitrend.support.crunchyroll.feature.authentication.ui.activity.AuthenticationScreen
import co.anitrend.support.crunchyroll.feature.authentication.ui.fragment.FragmentLogin
import co.anitrend.support.crunchyroll.feature.authentication.ui.fragment.FragmentLogout
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login.LoginViewModel
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.logout.LogoutViewModel
import co.anitrend.support.crunchyroll.navigation.Authentication
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val fragmentModule = module {
    scope<AuthenticationScreen> {
        fragment {
            FragmentLogin(
                presenter = get()
            )
        }
    }
    scope<AuthenticationScreen> {
        fragment {
            FragmentLogout()
        }
    }
}

private val presenterModule = module {
    factory {
        AuthPresenter(
            context = androidApplication(),
            settings = get()
        )
    }
}

private val viewModelModule = module {
    viewModel {
        LoginViewModel(
            useCase = get()
        )
    }
    viewModel {
        LogoutViewModel(
            useCase = get()
        )
    }
}

private val featureModule = module {
    factory<Authentication.Provider> {
        FeatureProvider()
    }
}

val moduleHelper by lazy {
    DynamicFeatureModuleHelper(
        listOf(fragmentModule, presenterModule, viewModelModule, featureModule)
    )
}
