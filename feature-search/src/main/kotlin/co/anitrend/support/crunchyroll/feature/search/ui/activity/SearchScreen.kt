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
import co.anitrend.support.crunchyroll.core.ui.model.FragmentItem
import co.anitrend.multisearch.model.Search
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.ui.commit
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery
import co.anitrend.support.crunchyroll.feature.search.R
import co.anitrend.support.crunchyroll.feature.search.databinding.SearchActivityBinding
import co.anitrend.support.crunchyroll.feature.search.ui.fragment.SearchContentScreen
import co.anitrend.support.crunchyroll.feature.search.viewmodel.SeriesSearchViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchScreen : CrunchyActivity<SearchActivityBinding>() {

    private val viewModel by viewModel<SeriesSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchActivityBinding.inflate(layoutInflater)
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
        lifecycleScope.launchWhenResumed {
            requireBinding().multiSearch.searchChangeFlow()
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
                        is Search.Removed -> {}
                    }
                }.collect()
        }
        onUpdateUserInterface()
    }

    private fun onUpdateUserInterface() {
        currentFragmentTag = FragmentItem(
            fragment = SearchContentScreen::class.java
        ).commit(requireBinding().searchContent, this) {}
    }
}