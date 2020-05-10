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

package co.anitrend.support.crunchyroll.feature.collection.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.core.viewmodel.contract.ISupportViewModel
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.argument
import co.anitrend.arch.extension.empty
import co.anitrend.arch.recycler.common.DefaultClickableItem
import co.anitrend.arch.ui.fragment.paged.SupportFragmentPagedList
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.paged.CrunchyFragmentPaged
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection
import co.anitrend.support.crunchyroll.domain.collection.models.CrunchyCollectionQuery
import co.anitrend.support.crunchyroll.feature.collection.R
import co.anitrend.support.crunchyroll.feature.collection.ui.adapter.CollectionAdapter
import co.anitrend.support.crunchyroll.feature.collection.viewmodel.CollectionViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.androidx.viewmodel.ext.android.viewModel

class CollectionContentScreen(
    override val defaultSpanSize: Int = R.integer.single_list_size,
    override val stateConfig: StateLayoutConfig
) : CrunchyFragmentPaged<CrunchyCollection>() {

    private val payload
            by argument<NavigationTargets.Collection.Payload>(
                NavigationTargets.Collection.PAYLOAD
            )

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
        CollectionAdapter(
            resources = resources,
            stateConfiguration = stateConfig
        )
    }

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(
            this,
            Observer {
                onPostModelChange(it)
            }
        )
    }

    private val viewModel by viewModel<CollectionViewModel>()

    /**
     * Additional initialization to be done in this method, this method will be called in
     * [androidx.fragment.app.FragmentActivity.onCreate].
     *
     * @param savedInstanceState
     */
    @FlowPreview
    override fun initializeComponents(savedInstanceState: Bundle?) {
        super.initializeComponents(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            supportViewAdapter.clickableFlow.debounce(16)
                .filterIsInstance<DefaultClickableItem<CrunchyCollection>>()
                .collect {
                    val data = it.data
                    val payload = NavigationTargets.Media.Payload(
                        collectionThumbnail = data?.portraitImage,
                        collectionName = data?.name ?: String.empty(),
                        collectionId = data?.collectionId ?: 0
                    )
                    NavigationTargets.Media.invoke(it.view.context, payload)
                }
        }
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
                parameter = CrunchyCollectionQuery(
                    seriesId = it.seriesId
                )
            )
        } ?: supportStateLayout?.networkStateLiveData?.postValue(
            NetworkState.Error(
                heading = "Invalid fragment parameters ${Emote.Cry}",
                message = "Invalid or missing payload, request cannot be processed"
            )
        )
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