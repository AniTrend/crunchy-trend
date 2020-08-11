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

package co.anitrend.support.crunchyroll.feature.series.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.extension.ext.argument
import co.anitrend.arch.extension.ext.attachComponent
import co.anitrend.arch.extension.ext.detachComponent
import co.anitrend.arch.recycler.common.DefaultClickableItem
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.navigation.Season
import co.anitrend.support.crunchyroll.navigation.Series
import co.anitrend.support.crunchyroll.navigation.Discover
import co.anitrend.support.crunchyroll.core.ui.fragment.CrunchyFragment
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchySeriesBrowseFilter
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesDetailQuery
import co.anitrend.support.crunchyroll.feature.series.databinding.SeriesContentBinding
import co.anitrend.support.crunchyroll.feature.series.presenter.SeriesDetailPresenter
import co.anitrend.support.crunchyroll.feature.series.ui.adpter.SeriesGenreAdapter
import co.anitrend.support.crunchyroll.feature.series.viewmodel.SeriesDetailViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SeriesContentScreen : CrunchyFragment() {

    private val payload
            by argument<Series.Payload>(
                Series.extraKey
            )

    private lateinit var binding: SeriesContentBinding

    private val seriesGenreAdapter by lazy(UNSAFE) {
        SeriesGenreAdapter(
            resources = resources,
            stateConfiguration = StateLayoutConfig()
        )
    }

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null) {
                    binding.supportStateLayout.networkMutableStateFlow.value = NetworkState.Success
                    seriesGenreAdapter.submitList(it.genres)
                    presenter.setUp(it, binding)
                }
            }
        )
        viewModelState().networkState.observe(
            viewLifecycleOwner,
            Observer {
                if (!binding.supportStateLayout.isContent)
                    binding.supportStateLayout.networkMutableStateFlow.value = it
            }
        )
    }

    private val presenter by inject<SeriesDetailPresenter>()

    private val viewModel by viewModel<SeriesDetailViewModel>()

    /**
     * Additional initialization to be done in this method, this method will be called in
     * [androidx.fragment.app.FragmentActivity.onCreate].
     *
     * @param savedInstanceState
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenCreated {
            if (viewModelState().isEmpty())
                onFetchDataInitialize()
        }
        lifecycleScope.launchWhenResumed {
            binding.supportStateLayout.interactionStateFlow
                .filterNotNull()
                .debounce(DEBOUNCE_DURATION)
                .collect {
                    viewModelState().retry()
                }
        }
        lifecycleScope.launchWhenResumed {
            seriesGenreAdapter.clickableStateFlow.debounce(DEBOUNCE_DURATION)
                .filterIsInstance<DefaultClickableItem<String>>()
                .collect {
                    val genre = it.data
                    if (!genre.isNullOrEmpty()) {
                        val payload = Discover.Payload(
                            browseFilter = CrunchySeriesBrowseFilter.TAG,
                            filterOption = genre
                        )

                        Discover(context, payload)
                    }
                }
        }
        lifecycleScope.launch {
            whenResumed {
                attachComponent(binding.seriesGenres)
            }
            lifecycle.whenStateAtLeast(Lifecycle.State.DESTROYED) {
                detachComponent(binding.seriesGenres)
            }
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
    ): View? {
        binding = SeriesContentBinding.inflate(inflater, container, false)
        binding.supportStateLayout.stateConfigFlow.value = get()
        return binding.root
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
        binding.seriesInfo.seriesSeasons.setOnClickListener {
            payload?.seriesId?.also { seriesId ->
                val collectionPayload = Season.Payload(
                    seriesId = seriesId
                )
                Season(
                    it.context, collectionPayload
                )
            }
        }

        presenter.setupGenresAdapter(
            binding.seriesGenres,
            seriesGenreAdapter
        )
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state

    private fun onFetchDataInitialize() {
        val seriesPayload = payload
        if (seriesPayload != null) {
            viewModel.state(
                parameter = CrunchySeriesDetailQuery(
                    seriesId = seriesPayload.seriesId
                )
            )
        } else {
            binding.supportStateLayout.networkMutableStateFlow.value =
                NetworkState.Error(
                    heading = "Invalid Parameter/s State",
                    message = "Invalid or missing payload"
                )
        }
    }
}