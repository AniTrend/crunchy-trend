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

package co.anitrend.support.crunchyroll.feature.series.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import co.anitrend.arch.core.viewmodel.contract.ISupportViewModel
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.argument
import co.anitrend.arch.ui.fragment.SupportFragmentPagedList
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection
import co.anitrend.support.crunchyroll.domain.collection.models.CrunchyCollectionQuery
import co.anitrend.support.crunchyroll.feature.series.R
import co.anitrend.support.crunchyroll.feature.series.presenter.SeriesDetailPresenter
import co.anitrend.support.crunchyroll.feature.series.ui.adpter.SeriesSeasonAdapter
import co.anitrend.support.crunchyroll.feature.series.viewmodel.SeriesCollectionViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SeriesCollectionScreen : SupportFragmentPagedList<CrunchyCollection, SeriesDetailPresenter, PagedList<CrunchyCollection>>() {

    private val payload
            by argument<NavigationTargets.Series.Payload>(
                NavigationTargets.Series.PAYLOAD
            )

    override val columnSize = R.integer.single_list_size

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
        SeriesSeasonAdapter(
            supportPresenter,
            supportStateConfiguration,
            object : ItemClickListener<CrunchyCollection> {
                override fun onItemClick(target: View, data: Pair<Int, CrunchyCollection?>) {
                    val payload = NavigationTargets.Media.Payload(
                        collectionId = data.second?.collectionId ?: 0
                    )
                    NavigationTargets.Media.invoke(target.context, payload)
                }

                override fun onItemLongClick(
                    target: View,
                    data: Pair<Int, CrunchyCollection?>
                ) {

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
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<SeriesDetailPresenter>()

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return view model of the given type
     */
    override val supportViewModel by viewModel<SeriesCollectionViewModel>()

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
        payload?.also {
            supportViewModel(
                parameter = CrunchyCollectionQuery(
                    seriesId = it.seriesId
                )
            )
        } ?: supportStateLayout?.setNetworkState(
            NetworkState.Error(
                heading = "Invalid Parameter/s State",
                message = "Invalid or missing payload"
            )
        )
    }

    /**
     * State configuration for any underlying state representing widgets
     */
    override val supportStateConfiguration by inject<SupportStateLayoutConfiguration>()

    companion object {
        const val FRAGMENT_TAG = "SeriesCollectionScreen"

        fun newInstance(bundle: Bundle?): SeriesCollectionScreen {
            return SeriesCollectionScreen().apply {
                arguments = bundle
            }
        }
    }
}