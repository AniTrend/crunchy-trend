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
import androidx.fragment.app.commit
import co.anitrend.arch.ui.common.ISupportActionUp
import co.anitrend.multisearch.model.MultiSearchChangeListener
import co.anitrend.support.crunchyroll.core.android.widgets.ElasticDragDismissFrameLayout
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery
import co.anitrend.support.crunchyroll.feature.search.R
import co.anitrend.support.crunchyroll.feature.search.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.search.ui.fragment.SearchContentScreen
import co.anitrend.support.crunchyroll.feature.search.viewmodel.SeriesSearchViewModel
import kotlinx.android.synthetic.main.search_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchScreen : CrunchyActivity() {

    override val elasticLayout: ElasticDragDismissFrameLayout?
        get() = draggableFrame

    private val multiSearchViewListener =
        object : MultiSearchChangeListener {

            /**
             * Called when a search item has been selected, or when an item has been removed
             * and a new selection is made
             *
             * @param index character index that has been changed
             * @param charSequence stream of characters including changes
             */
            override fun onItemSelected(index: Int, charSequence: CharSequence) {
                viewModel.searchQueryLiveData.postValue(
                    CrunchySeriesSearchQuery(
                        searchTerm = charSequence.toString()
                    )
                )
            }

            /**
             * Called when an IME action of done is triggered
             *
             * @param index character index that has been changed
             * @param charSequence stream of characters including changes
             */
            override fun onSearchComplete(index: Int, charSequence: CharSequence) {

            }

            /**
             * Called when a search item has been removed
             *
             * @param index
             */
            override fun onSearchItemRemoved(index: Int) {

            }

            /**
             * Called when text has been changed
             *
             * @param index character index that has been changed
             * @param charSequence stream of characters including changes
             */
            override fun onTextChanged(index: Int, charSequence: CharSequence) {
                if (charSequence.isNotBlank())
                    viewModel.searchQueryLiveData.postValue(
                        CrunchySeriesSearchQuery(
                            searchTerm = charSequence.toString()
                        )
                    )
            }
        }

    private val viewModel by viewModel<SeriesSearchViewModel>()

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
        multiSearch.setSearchViewListener(multiSearchViewListener)
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        val target = supportFragmentManager.findFragmentByTag(
            SearchContentScreen.fragmentTag
        ) ?: SearchContentScreen.newInstance()

        supportActionUp = target as ISupportActionUp

        supportFragmentManager.commit {
            replace(R.id.search_content, target, SearchContentScreen.fragmentTag)
        }
    }
}