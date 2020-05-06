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

    override fun initializeComponents(savedInstanceState: Bundle?) {
        injectFeatureModules()
    }

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

    override fun onUpdateUserInterface() {

    }

    override fun onFetchDataInitialize() {
        viewModel.viewModelLists.forEach {
            it.requestIfModelIsNotInitialized()
        }
    }

    override fun onDestroyView() {
        binding.supportRecycler.adapter = null
        super.onDestroyView()
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState(): Nothing? = null
}