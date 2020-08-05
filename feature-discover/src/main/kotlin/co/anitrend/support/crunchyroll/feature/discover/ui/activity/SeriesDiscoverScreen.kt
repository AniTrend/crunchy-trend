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

package co.anitrend.support.crunchyroll.feature.discover.ui.activity

import android.os.Bundle
import co.anitrend.support.crunchyroll.core.extensions.commit
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.ui.fragment.model.FragmentItem
import co.anitrend.support.crunchyroll.feature.discover.R
import co.anitrend.support.crunchyroll.feature.discover.koin.moduleHelper
import co.anitrend.support.crunchyroll.feature.discover.ui.fragment.SeriesDiscoverContent
import kotlinx.android.synthetic.main.discover_activity.*
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope

class SeriesDiscoverScreen : CrunchyActivity() {

    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {
        super.configureActivity()
        setupKoinFragmentFactory(lifecycleScope)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_activity)
        setSupportActionBar(bottomAppBar)
    }

    /**
     * Additional initialization to be done in this method, this is called in during
     * [androidx.fragment.app.FragmentActivity.onPostCreate]
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
        val target = FragmentItem(
            parameter = intent.extras,
            fragment = SeriesDiscoverContent::class.java
        )

        currentFragmentTag = supportFragmentManager.commit(R.id.discover_content, target) {
            //setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }
}