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

package co.anitrend.support.crunchyroll.feature.search.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.paging.PagedList
import co.anitrend.arch.core.viewmodel.contract.ISupportViewModel
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.android.widgets.ElasticDragDismissFrameLayout
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery
import co.anitrend.support.crunchyroll.feature.search.R
import co.anitrend.support.crunchyroll.feature.search.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.search.presenter.SeriesPresenter
import co.anitrend.support.crunchyroll.feature.search.ui.fragment.SearchContentScreen
import co.anitrend.support.crunchyroll.feature.search.viewmodel.SeriesSearchViewModel
import com.iammert.library.ui.multisearchviewlib.MultiSearchView
import kotlinx.android.synthetic.main.search_activity.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchScreen : CrunchyActivity<PagedList<CrunchySeries>, SeriesPresenter>() {

    private lateinit var systemChromeFader: ElasticDragDismissFrameLayout.SystemChromeFader

    private val multiSearchViewListener =
        object : MultiSearchView.MultiSearchViewListener {
            override fun onItemSelected(index: Int, s: CharSequence) {
                supportViewModel.searchQueryLiveData.postValue(
                    CrunchySeriesSearchQuery(
                        query = s.toString()
                    )
                )
            }

            override fun onSearchComplete(index: Int, s: CharSequence) {
                /*supportViewModel.searchQueryLiveData.postValue(
                    CrunchySeriesSearchQuery(
                        query = s.toString()
                    )
                )*/
            }

            override fun onSearchItemRemoved(index: Int) {

            }

            override fun onTextChanged(index: Int, s: CharSequence) {
                if (s.isNotBlank())
                    supportViewModel.searchQueryLiveData.postValue(
                        CrunchySeriesSearchQuery(
                            query = s.toString()
                        )
                    )
            }
    }

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<SeriesPresenter>()

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return view model of the given type
     */
    override val supportViewModel by viewModel<SeriesSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_activity)
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
        multiSearchView.setSearchViewListener(multiSearchViewListener)
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
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        supportFragmentActivity = SearchContentScreen()
        val target = supportFragmentActivity as SupportFragment<*, *, *>

        supportFragmentManager.commit {
            replace(R.id.search_content, target, target.tag)
        }
    }
}