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

package co.anitrend.support.crunchyroll.feature.discover.ui.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.extension.ext.argument
import co.anitrend.arch.recycler.common.ClickableItem
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.navigation.*
import co.anitrend.support.crunchyroll.core.ui.fragment.list.CrunchyFragmentList
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchySeriesBrowseFilter
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesBrowseQuery
import co.anitrend.support.crunchyroll.feature.discover.R
import co.anitrend.support.crunchyroll.feature.discover.koin.moduleHelper
import co.anitrend.support.crunchyroll.feature.discover.ui.activity.SeriesDiscoverScreen
import co.anitrend.support.crunchyroll.feature.discover.viewmodel.SeriesDiscoverViewModel
import co.anitrend.support.crunchyroll.shared.series.adapter.SeriesViewAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SeriesDiscoverContent(
    override val defaultSpanSize: Int = R.integer.single_list_size
) : CrunchyFragmentList<CrunchySeries>() {

    private val payload: Discover.Payload?
            by argument(Discover.extraKey)

    private val viewModel by viewModel<SeriesDiscoverViewModel>()

    override val stateConfig: StateLayoutConfig by inject()

    override val supportViewAdapter by lazy(UNSAFE) {
        SeriesViewAdapter(
            resources = resources,
            stateConfiguration = stateConfig
        )
    }

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(
            viewLifecycleOwner,
            {
                onPostModelChange(it)
            }
        )
    }

    /**
     * Additional initialization to be done in this method, this method will be called in
     * [androidx.fragment.app.FragmentActivity.onCreate].
     *
     * @param savedInstanceState
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {
        super.initializeComponents(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            supportViewAdapter.clickableFlow.debounce(DEBOUNCE_DURATION)
                .filterIsInstance<ClickableItem.Data<CrunchySeries>>()
                .collect {
                    val data = it.data
                    val view = it.view

                    val payload = Series.Payload(
                        seriesId = data.seriesId
                    )

                    Series(view.context, payload)
                }
        }
    }

    override fun onFetchDataInitialize() {
        val query = payload?.let {
            CrunchySeriesBrowseQuery(
                filter = it.browseFilter,
                option = it.filterOption
            )
        } ?: CrunchySeriesBrowseQuery(
            filter = CrunchySeriesBrowseFilter.ALPHA
        )

        viewModel.state(
            parameter = query
        )
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state
}