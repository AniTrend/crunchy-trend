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
import androidx.lifecycle.lifecycleScope
import co.anitrend.multisearch.model.Search
import co.anitrend.support.crunchyroll.core.extensions.commit
import co.anitrend.support.crunchyroll.core.extensions.koinScope
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.ui.fragment.model.FragmentItem
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery
import co.anitrend.support.crunchyroll.feature.search.R
import co.anitrend.support.crunchyroll.feature.search.ui.fragment.SearchContentScreen
import co.anitrend.support.crunchyroll.feature.search.viewmodel.SeriesSearchViewModel
import kotlinx.android.synthetic.main.search_activity.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchScreen : CrunchyActivity() {

    private val viewModel by viewModel<SeriesSearchViewModel>()

    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {
        super.configureActivity()
        setupKoinFragmentFactory(koinScope)
    }

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
        lifecycleScope.launchWhenResumed {
            multiSearch.searchChangeFlow()
                .filterNotNull()
                .onEach { search ->
                    when (search) {
                        is Search.TextChanged -> {
                            if (search.text.isNotBlank())
                                viewModel.searchQueryLiveData.postValue(
                                    CrunchySeriesSearchQuery(
                                        searchTerm = search.text.toString()
                                    )
                                )
                        }
                        is Search.Selected -> {
                            viewModel.searchQueryLiveData.postValue(
                                CrunchySeriesSearchQuery(
                                    searchTerm = search.text.toString()
                                )
                            )
                        }
                    }
                }.collect()
        }
        onUpdateUserInterface()
    }

    private fun onUpdateUserInterface() {
        val target = FragmentItem(
            fragment = SearchContentScreen::class.java
        )

        currentFragmentTag = supportFragmentManager.commit(R.id.search_content, target) {}
    }
}