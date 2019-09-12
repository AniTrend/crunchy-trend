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

package co.anitrend.support.crunchyroll.ui.contract

import androidx.appcompat.app.AppCompatDelegate
import co.anitrend.support.crunchyroll.App
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import co.anitrend.arch.core.presenter.SupportPresenter
import co.anitrend.arch.ui.activity.SupportActivity

abstract class CrunchyActivity<M, P : SupportPresenter<CrunchySettings>> : SupportActivity<M, P>() {

    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {

    }

    /**
     * Toggles the theme for the application
     */
    protected fun toggleTheme() {
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> supportPresenter.supportPreference.isLightTheme = true
            else -> supportPresenter.supportPreference.isLightTheme = false
        }
        (applicationContext as App).applyTheme()
        recreate()
    }

    /**
     * Handles the complex logic required to dispatch network request to [SupportViewModel]
     * to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportViewModel.model]
     *
     * @see [SupportViewModel.requestBundleLiveData]
     */
    override fun onFetchDataInitialize() {

    }
}