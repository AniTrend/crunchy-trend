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

package co.anitrend.support.crunchyroll.feature.authentication.ui.activity

import android.os.Bundle
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.commit
import co.anitrend.support.crunchyroll.core.ui.inject
import co.anitrend.support.crunchyroll.core.ui.model.FragmentItem
import co.anitrend.support.crunchyroll.feature.authentication.authenticator.view.AuthenticatorScreen
import co.anitrend.support.crunchyroll.feature.authentication.databinding.ActivityAuthBinding
import co.anitrend.support.crunchyroll.feature.authentication.ui.fragment.FragmentLogin
import co.anitrend.support.crunchyroll.feature.authentication.ui.fragment.FragmentLogout

class AuthenticationScreen : AuthenticatorScreen<ActivityAuthBinding>() {

    private val presenter by inject<CrunchyCorePresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(requireBinding().root)
        setSupportActionBar(requireBinding().bottomAppBar)
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
        onUpdateUserInterface()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    private fun onUpdateUserInterface() {
        val target = when (presenter.settings.isAuthenticated.value) {
            true -> {
                FragmentItem(
                    parameter = intent.extras,
                    fragment = FragmentLogout::class.java
                )
            }
            else -> {
                FragmentItem(
                    parameter = intent.extras,
                    fragment = FragmentLogin::class.java
                )
            }
        }

        currentFragmentTag = target.commit(
            requireBinding().contentFrame,
            this
        ) {}
    }
}