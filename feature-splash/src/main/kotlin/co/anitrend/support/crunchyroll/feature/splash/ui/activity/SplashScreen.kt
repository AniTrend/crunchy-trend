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

package co.anitrend.support.crunchyroll.feature.splash.ui.activity

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.extension.ext.isStateAtLeast
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.navigation.*
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.feature.splash.R
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject

class SplashScreen : CrunchyActivity() {

    private val presenter by inject<CrunchyCorePresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    /**
     * Additional initialization to be done in this method, if the overriding class is type of
     * [androidx.fragment.app.Fragment] then this method will be called in
     * [androidx.fragment.app.FragmentActivity.onCreate]. Otherwise
     * [androidx.fragment.app.FragmentActivity.onPostCreate] invokes this function
     *
     * @param savedInstanceState
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            onUpdateUserInterface()
        }
    }

    private suspend fun onUpdateUserInterface() {
        delay(500)
        if (!presenter.settings.isNewInstallation)
            Main(applicationContext)
        else {
            if (presenter.settings.isAuthenticated)
                Main(applicationContext)
            else {
                presenter.settings.isNewInstallation = false
                Authentication(applicationContext)
            }
        }
        closeScreen()
    }
}