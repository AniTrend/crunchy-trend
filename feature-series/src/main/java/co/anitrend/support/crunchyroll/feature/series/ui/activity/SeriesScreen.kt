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
import androidx.fragment.app.commit
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.android.widgets.ElasticDragDismissFrameLayout
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.feature.series.R
import co.anitrend.support.crunchyroll.feature.series.common.ISwappable
import co.anitrend.support.crunchyroll.feature.series.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.series.presenter.SeriesDetailPresenter
import co.anitrend.support.crunchyroll.feature.series.ui.fragment.SeriesCollectionScreen
import co.anitrend.support.crunchyroll.feature.series.ui.fragment.SeriesContentScreen
import kotlinx.android.synthetic.main.series_activity.*
import org.koin.android.ext.android.inject

class SeriesScreen : CrunchyActivity<CrunchySeries?, SeriesDetailPresenter>(), ISwappable {

    private lateinit var systemChromeFader: ElasticDragDismissFrameLayout.SystemChromeFader

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<SeriesDetailPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.series_activity)
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
        systemChromeFader =
            object : ElasticDragDismissFrameLayout.SystemChromeFader(
                this
            ) {
                override fun onDragDismissed() {
                    closeScreen()
                }
            }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */
    override fun onResume() {
        super.onResume()
        draggableFrame.addListener(systemChromeFader)
    }

    /**
     * Dispatch onPause() to fragments.
     */
    override fun onPause() {
        draggableFrame.removeListener(systemChromeFader)
        super.onPause()
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    override fun onBackPressed() {
        if (supportFragmentActivity is SeriesCollectionScreen)
            onSwapWithDetail()
        else
            super.onBackPressed()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        onSwapWithDetail()
    }

    override fun onSwapWithCollection() {
        val fragment = supportFragmentManager.findFragmentByTag(
            SeriesCollectionScreen.FRAGMENT_TAG
        )

        supportFragmentActivity = if (fragment is SeriesCollectionScreen)
            fragment
        else
            SeriesCollectionScreen.newInstance(intent.extras)

        val target = supportFragmentActivity as SupportFragment<*, *, *>

        supportFragmentManager.commit {
            //setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.series_content, target, SeriesCollectionScreen.FRAGMENT_TAG)
        }
    }

    private fun onSwapWithDetail() {
        val fragment = supportFragmentManager.findFragmentByTag(
            SeriesContentScreen.FRAGMENT_TAG
        )

        supportFragmentActivity = if (fragment is SeriesContentScreen)
            fragment
        else
            SeriesContentScreen.newInstance(intent.extras)

        val target = supportFragmentActivity as SupportFragment<*, *, *>

        supportFragmentManager.commit {
            //setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.series_content, target, SeriesContentScreen.FRAGMENT_TAG)
        }
    }
}