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

package co.anitrend.support.crunchyroll.feature.search.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.recycler.common.DefaultClickableItem
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.list.CrunchyFragmentList
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.feature.search.R
import co.anitrend.support.crunchyroll.feature.search.viewmodel.SeriesSearchViewModel
import co.anitrend.support.crunchyroll.shared.series.adapter.SeriesViewAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SearchContentScreen(
    override val defaultSpanSize: Int = R.integer.single_list_size
) : CrunchyFragmentList<CrunchySeries>() {

    private val viewModel by sharedViewModel<SeriesSearchViewModel>()

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
        SeriesViewAdapter(
            resources = resources,
            stateConfiguration = get()
        )
    }

    /**
     * Invoke view model observer to watch for changes
     */
    @ExperimentalCoroutinesApi
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(
            viewLifecycleOwner,
            Observer {
                onPostModelChange(it)
            }
        )
        viewModel.searchQueryLiveData.observe(
            viewLifecycleOwner,
            Observer {
                onFetchDataInitialize()
            }
        )
    }

    /**
     * Called immediately after [onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created. The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by [onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportStateLayout?.networkMutableStateFlow?.value =
            NetworkState.Error(
                heading = "Looking for something?",
                message = "Tap the ${Emote.Search} to get started"
            )

    }

    /**
     * Additional initialization to be done in this method, this method will be called in
     * [androidx.fragment.app.FragmentActivity.onCreate].
     *
     * @param savedInstanceState
     */
    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun initializeComponents(savedInstanceState: Bundle?) {
        super.initializeComponents(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            supportViewAdapter.clickableStateFlow.debounce(16)
                .filterIsInstance<DefaultClickableItem<CrunchySeries>>()
                .collect {
                    val data = it.data
                    val seriesPayload = NavigationTargets.Series.Payload(
                        seriesId = data?.seriesId ?: 0
                    )
                    NavigationTargets.Series(
                        it.view.context, seriesPayload
                    )
                }
        }
    }

    override fun onFetchDataInitialize() {
        val searchQuery = viewModel.searchQueryLiveData.value
        if (searchQuery != null) {
            viewModel.state(
                parameter = searchQuery
            )
        }
    }

    /**
     * State configuration for any underlying state representing widgets
     */
    override val stateConfig = StateLayoutConfig(
        loadingDrawable = R.drawable.ic_launcher_foreground,
        errorDrawable = R.drawable.ic_support_empty_state
    )

    /**
     * Called when the view previously created by [.onCreateView] has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after [onStop] and before [onDestroy]. It is called
     * *regardless* of whether [onCreateView] returned a
     * non-null view. Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        supportRecyclerView?.adapter = null
        super.onDestroyView()
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state

    /**
     * Expects a module helper if one is available for the current scope, otherwise return null
     */
    override fun featureModuleHelper(): Nothing? = null
}