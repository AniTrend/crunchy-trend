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
import androidx.lifecycle.Observer
import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.core.viewmodel.contract.ISupportViewModel
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.argument
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.IFragmentFactory
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchySeriesBrowseFilter
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesDetailQuery
import co.anitrend.support.crunchyroll.feature.series.databinding.SeriesContentBinding
import co.anitrend.support.crunchyroll.feature.series.presenter.SeriesDetailPresenter
import co.anitrend.support.crunchyroll.feature.series.ui.adpter.SeriesGenreAdapter
import co.anitrend.support.crunchyroll.feature.series.viewmodel.SeriesDetailViewModel
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SeriesContentScreen : SupportFragment<CrunchySeries>() {

    private val payload
            by argument<NavigationTargets.Series.Payload>(
                NavigationTargets.Series.PAYLOAD
            )

    private lateinit var binding: SeriesContentBinding

    private val seriesGenreAdapter by lazy(LAZY_MODE_UNSAFE) {
        SeriesGenreAdapter(
            object : ItemClickListener<String> {
                /**
                 * When the target view from [View.OnClickListener]
                 * is clicked from a view holder this method will be called
                 *
                 * @param target view that has been clicked
                 * @param data the liveData that at the click index
                 */
                override fun onItemClick(target: View, data: Pair<Int, String?>) {
                    val genre = data.second
                    if (!genre.isNullOrEmpty()) {
                        val payload = NavigationTargets.Discover.Payload(
                            browseFilter = CrunchySeriesBrowseFilter.TAG,
                            filterOption = genre
                        )

                        NavigationTargets.DiscoverScreen(context, payload)
                    }
                }

                /**
                 * When the target view from [View.OnLongClickListener]
                 * is clicked from a view holder this method will be called
                 *
                 * @param target view that has been long clicked
                 * @param data the liveData that at the long click index
                 */
                override fun onItemLongClick(target: View, data: Pair<Int, String?>) {

                }
            },
            get()
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
                    binding.supportStateLayout.setNetworkState(NetworkState.Success)
                    seriesGenreAdapter.submitList(it.genres)
                }
            }
        )
        viewModelState().networkState.observe(
            viewLifecycleOwner,
            Observer {
                binding.supportStateLayout.setNetworkState(it)
            }
        )
        binding.supportStateLayout.interactionLiveData.observe(
            viewLifecycleOwner,
            Observer {
                viewModelState().retry()
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

        binding.supportStateLayout.stateConfig = get()

        binding.lifecycleOwner = this
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
        binding.viewModel = viewModel
        binding.presenter = presenter

        binding.seriesInfo.seriesSeasons.setOnClickListener {
            payload?.seriesId?.also { seriesId ->
                val collectionPayload = NavigationTargets.Collection.Payload(
                    seriesId = seriesId
                )
                NavigationTargets.Collection(
                    it.context, collectionPayload
                )
            }
        }

        presenter.setupGenresAdapter(
            binding.seriesGenres,
            seriesGenreAdapter
        )
    }

    override fun onResume() {
        super.onResume()
        if (!viewModelState().isEmpty())
            onFetchDataInitialize()
        else
            onUpdateUserInterface()
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state

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
            viewModel.state(
                parameter = CrunchySeriesDetailQuery(
                    seriesId = it.seriesId
                )
            )
        } ?: binding.supportStateLayout.setNetworkState(
            NetworkState.Error(
                heading = "Invalid Parameter/s State",
                message = "Invalid or missing payload"
            )
        )
    }

    companion object : IFragmentFactory<SeriesContentScreen> {
        override val FRAGMENT_TAG = SeriesContentScreen::class.java.simpleName

        override fun newInstance(bundle: Bundle?) =
            SeriesContentScreen().apply {
                arguments = bundle
            }
    }
}