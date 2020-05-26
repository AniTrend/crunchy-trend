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

package co.anitrend.support.crunchyroll.feature.series.ui.activity

import android.os.Bundle
import co.anitrend.support.crunchyroll.core.android.widgets.ElasticDragDismissFrameLayout
import co.anitrend.support.crunchyroll.core.extensions.commit
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.ui.fragment.model.FragmentItem
import co.anitrend.support.crunchyroll.feature.series.R
import co.anitrend.support.crunchyroll.feature.series.koin.moduleHelper
import co.anitrend.support.crunchyroll.feature.series.ui.fragment.SeriesContentScreen
import kotlinx.android.synthetic.main.series_activity.*
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.lifecycleScope

class SeriesScreen : CrunchyActivity() {

    override val elasticLayout: ElasticDragDismissFrameLayout
        get() = draggableFrame

    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {
        super.configureActivity()
        setupKoinFragmentFactory(lifecycleScope)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.series_activity)
        setSupportActionBar(bottomAppBar)
        onUpdateUserInterface()
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

    }

    /**
     * Expects a module helper if one is available for the current scope, otherwise return null
     */
    override fun featureModuleHelper() = moduleHelper

    private fun onUpdateUserInterface() {
        val target = FragmentItem(
            parameter = intent.extras,
            fragment = SeriesContentScreen::class.java
        )

        currentFragmentTag = supportFragmentManager.commit(series_content.id, target) {
            //setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }
}