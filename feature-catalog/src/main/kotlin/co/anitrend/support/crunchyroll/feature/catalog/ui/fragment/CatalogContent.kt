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
import androidx.lifecycle.whenResumed
import androidx.recyclerview.widget.GridLayoutManager
import co.anitrend.arch.core.viewmodel.contract.ISupportViewModel
import co.anitrend.arch.extension.getCompatColor
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.feature.catalog.R
import co.anitrend.support.crunchyroll.feature.catalog.controller.decorator.HeaderDecorator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import co.anitrend.support.crunchyroll.feature.catalog.controller.group.CarouselGroup
import co.anitrend.support.crunchyroll.feature.catalog.controller.items.CatalogItem
import co.anitrend.support.crunchyroll.feature.catalog.controller.items.HeaderItem
import co.anitrend.support.crunchyroll.feature.catalog.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.catalog.presenter.CatalogPresenter
import co.anitrend.support.crunchyroll.feature.catalog.viewmodel.CatalogViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import co.anitrend.support.crunchyroll.feature.catalog.databinding.ContentSeriesCatalogBinding

class CatalogContent : SupportFragment<List<CrunchyCatalogWithSeries>, CatalogPresenter, List<CrunchyCatalogWithSeries>>() {

    private lateinit var binding: ContentSeriesCatalogBinding

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var gridLayoutManager: GridLayoutManager

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<CatalogPresenter>()

    private val viewModel by viewModel<CatalogViewModel>()

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModel.mediatorLiveData.observe(viewLifecycleOwner, Observer {  catalog ->
            if (catalog.series.isNotEmpty()) {
                val section = Section(
                    HeaderItem(
                        catalog.qualifier
                    )
                )
                section.setHideWhenEmpty(true)
                val groupAdapter = GroupAdapter<GroupieViewHolder>()
                catalog.series.forEach { series ->
                    groupAdapter.add(
                        CatalogItem(
                            series
                        )
                    )
                }
                val carouselGroup =
                    CarouselGroup(
                        groupAdapter
                    )
                section.add(carouselGroup)
                groupAdapter.add(section)

                onUpdateUserInterface()
            }
        })
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
        launch {
            lifecycle.whenResumed { onFetchDataInitialize() }
        }
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
        setUpViewModelObserver()

        gridLayoutManager = GridLayoutManager(context, groupAdapter.spanCount)
        gridLayoutManager.spanSizeLookup = groupAdapter.spanSizeLookup
        with (binding.supportRecycler) {
            layoutManager = gridLayoutManager
            addItemDecoration(
                HeaderDecorator(
                    context.getCompatColor(R.color.secondaryTextColor),
                    resources.getDimensionPixelSize(R.dimen.lg_margin)
                )
            )
            adapter = groupAdapter
        }
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
        if (!viewModel.hasModelData()) {
            viewModel.viewStateFeatured()
            viewModel.viewStateNewest()
            viewModel.viewStatePopular()
            viewModel.viewStateSimulcast()
            viewModel.viewStateUpdated()
        }
    }
}