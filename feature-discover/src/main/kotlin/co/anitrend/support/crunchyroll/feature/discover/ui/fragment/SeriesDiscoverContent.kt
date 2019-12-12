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
import android.view.View
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import co.anitrend.arch.core.viewmodel.contract.ISupportViewModel
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.ui.fragment.SupportFragmentPagedList
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchySeriesFilter
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesBrowseQuery
import co.anitrend.support.crunchyroll.feature.discover.R
import co.anitrend.support.crunchyroll.feature.discover.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.discover.presenter.SeriesPresenter
import co.anitrend.support.crunchyroll.feature.discover.ui.adapter.SeriesViewAdapter
import co.anitrend.support.crunchyroll.feature.discover.viewmodel.SeriesDiscoverViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SeriesDiscoverContent : SupportFragmentPagedList<CrunchySeries, SeriesPresenter, PagedList<CrunchySeries>>() {

    override val columnSize = R.integer.single_list_size

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
    override val supportViewModel by viewModel<SeriesDiscoverViewModel>()

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
        SeriesViewAdapter(
            supportPresenter,
            object : ItemClickListener<CrunchySeries> {
                /**
                 * When the target view from [View.OnClickListener]
                 * is clicked from a view holder this method will be called
                 *
                 * @param target view that has been clicked
                 * @param data the liveData that at the click index
                 */
                /**
                 * When the target view from [View.OnClickListener]
                 * is clicked from a view holder this method will be called
                 *
                 * @param target view that has been clicked
                 * @param data the liveData that at the click index
                 */
                override fun onItemClick(target: View, data: Pair<Int, CrunchySeries?>) {

                }

                /**
                 * When the target view from [View.OnLongClickListener]
                 * is clicked from a view holder this method will be called
                 *
                 * @param target view that has been long clicked
                 * @param data the liveData that at the long click index
                 */
                /**
                 * When the target view from [View.OnLongClickListener]
                 * is clicked from a view holder this method will be called
                 *
                 * @param target view that has been long clicked
                 * @param data the liveData that at the long click index
                 */
                override fun onItemLongClick(target: View, data: Pair<Int, CrunchySeries?>) {

                }
            }
        )
    }

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(
            this,
            Observer {
                onPostModelChange(it)
            }
        )
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
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {

    }

    /**
     * Handles the complex logic required to dispatch network request to [ISupportViewModel]
     * to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [ISupportViewModel.model]
     *
     * @see [ISupportViewModel.invoke]
     */
    override fun onFetchDataInitialize() {
        supportViewModel(
            parameter = CrunchySeriesBrowseQuery(
                filter = CrunchySeriesFilter.NEWEST.attribute
            )
        )
    }

    /**
     * State configuration for any underlying state representing widgets
     */
    override val supportStateConfiguration by inject<SupportStateLayoutConfiguration>()
}