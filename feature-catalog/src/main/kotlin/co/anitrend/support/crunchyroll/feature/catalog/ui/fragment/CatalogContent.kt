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

package co.anitrend.support.crunchyroll.feature.catalog.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.core.viewmodel.contract.ISupportViewModel
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.feature.catalog.R
import co.anitrend.support.crunchyroll.feature.catalog.controller.decorator.HeaderDecorator
import co.anitrend.support.crunchyroll.feature.catalog.databinding.ContentSeriesCatalogBinding
import co.anitrend.support.crunchyroll.feature.catalog.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.catalog.presenter.CatalogPresenter
import co.anitrend.support.crunchyroll.feature.catalog.viewmodel.CatalogViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatalogContent : SupportFragment<List<CrunchyCatalogWithSeries>>() {

    private lateinit var binding: ContentSeriesCatalogBinding

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var gridLayoutManager: GridLayoutManager

    private val presenter by inject<CatalogPresenter>()

    private val viewModel by viewModel<CatalogViewModel>()

    private fun CrunchyCatalogWithSeries.networkState() =
        if (series.isEmpty()) NetworkState.Loading else NetworkState.Success

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModel.viewModelLists.forEach { vm ->
            vm.model.observe(viewLifecycleOwner, Observer { model ->
                presenter.setUpGroupAdapter(
                    model,
                    groupAdapter
                )
            })

            vm.networkState?.observe(viewLifecycleOwner, Observer { networkState ->
                presenter.updatePlaceHolderState(
                    { vm.retry() },
                    networkState,
                    vm.model.value,
                    groupAdapter
                )
            })
        }
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
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between
     * [onCreate] & [onActivityCreated].
     *
     * A default View can be returned by calling [Fragment] in your
     * constructor. Otherwise, this method returns null.
     *
     * It is recommended to __only__ inflate the layout in this method and move
     * logic that operates on the returned View to [onViewCreated].
     *
     * If you return a View from here, you will later be called in [onDestroyView]
     * when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be
     * attached to.  The fragment should not add the view itself, but this can be used to generate
     * the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ContentSeriesCatalogBinding.inflate(
        inflater, container, false
    ).let {
        binding = it
        it.root
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     * @param view The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridLayoutManager = GridLayoutManager(context, groupAdapter.spanCount)
        gridLayoutManager.spanSizeLookup = groupAdapter.spanSizeLookup
        with (binding.supportRecycler) {
            layoutManager = gridLayoutManager
            addItemDecoration(
                HeaderDecorator(
                    resources.getDimensionPixelSize(R.dimen.lg_margin)
                )
            )
            adapter = groupAdapter
        }
        binding.supportRefreshLayout.setOnRefreshListener {
            binding.supportRefreshLayout.isRefreshing = false
            viewModel.viewModelLists.forEach {
                it.retry()
            }
        }
        onFetchDataInitialize()
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
        viewModel.viewModelLists.forEach {
            launch {
                it.requestIfModelIsNotInitialized()
                delay(500)
            }
        }
    }

    /**
     * Called when the view previously created by [.onCreateView] has
     * been detached from the fragment.  The next time the fragment needs
     * to be displayed, a new view will be created.  This is called
     * after [.onStop] and before [.onDestroy].  It is called
     * *regardless* of whether [.onCreateView] returned a
     * non-null view.  Internally it is called after the view's state has
     * been saved but before it has been removed from its parent.
     */
    override fun onDestroyView() {
        binding.supportRecycler.adapter = null
        super.onDestroyView()
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState(): Nothing? = null
}