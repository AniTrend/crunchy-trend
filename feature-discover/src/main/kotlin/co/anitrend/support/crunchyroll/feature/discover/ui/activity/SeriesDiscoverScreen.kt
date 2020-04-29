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
import androidx.fragment.app.commit
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.android.widgets.ElasticDragDismissFrameLayout
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.feature.discover.R
import co.anitrend.support.crunchyroll.feature.discover.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.discover.presenter.SeriesPresenter
import co.anitrend.support.crunchyroll.feature.discover.ui.fragment.SeriesDiscoverContent
import kotlinx.android.synthetic.main.discover_activity.*
import org.koin.android.ext.android.inject

class SeriesDiscoverScreen : CrunchyActivity<Nothing, SeriesPresenter>() {

    override val elasticLayout: ElasticDragDismissFrameLayout?
        get() = draggableFrame

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<SeriesPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.discover_activity)
        setSupportActionBar(bottomAppBar)
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
        injectFeatureModules()
        onUpdateUserInterface()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        val target = supportFragmentManager.findFragmentByTag(
            SeriesDiscoverContent.FRAGMENT_TAG
        ) ?: SeriesDiscoverContent.newInstance(intent.extras)

        supportFragmentActivity = target as SupportFragment<*, *, *>

        supportFragmentManager.commit {
            //setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.discover_content, target, SeriesDiscoverContent.FRAGMENT_TAG)
        }
    }
}