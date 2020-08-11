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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.extension.ext.argument
import co.anitrend.arch.extension.ext.empty
import co.anitrend.arch.recycler.common.DefaultClickableItem
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.navigation.Season
import co.anitrend.support.crunchyroll.navigation.Media
import co.anitrend.support.crunchyroll.core.ui.fragment.list.CrunchyFragmentList
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection
import co.anitrend.support.crunchyroll.domain.collection.models.CrunchyCollectionQuery
import co.anitrend.support.crunchyroll.feature.collection.R
import co.anitrend.support.crunchyroll.feature.collection.ui.adapter.CollectionAdapter
import co.anitrend.support.crunchyroll.feature.collection.viewmodel.CollectionViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.androidx.viewmodel.ext.android.viewModel

class CollectionContentScreen(
    override val defaultSpanSize: Int = R.integer.single_list_size,
    override val stateConfig: StateLayoutConfig
) : CrunchyFragmentList<CrunchyCollection>() {

    private val payload
            by argument<Season.Payload>(
                Season.extraKey
            )

    override val supportViewAdapter by lazy(UNSAFE) {
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
            viewLifecycleOwner,
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
    override fun initializeComponents(savedInstanceState: Bundle?) {
        super.initializeComponents(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            supportViewAdapter.clickableStateFlow.debounce(DEBOUNCE_DURATION)
                .filterIsInstance<DefaultClickableItem<CrunchyCollection>>()
                .collect {
                    val data = it.data
                    val payload = Media.Payload(
                        collectionThumbnail = data?.portraitImage,
                        collectionName = data?.name ?: String.empty(),
                        collectionId = data?.collectionId ?: 0
                    )
                    Media.invoke(it.view.context, payload)
                }
        }
    }

    override fun onFetchDataInitialize() {
        val collectionPayload = payload
        if (collectionPayload != null) {
            viewModel.state(
                parameter = CrunchyCollectionQuery(
                    seriesId = collectionPayload.seriesId
                )
            )
        } else {
            supportStateLayout?.networkMutableStateFlow?.value =
                NetworkState.Error(
                    heading = "Invalid fragment parameters ${Emote.Cry}",
                    message = "Invalid or missing payload, request cannot be processed"

                )
        }
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state
}