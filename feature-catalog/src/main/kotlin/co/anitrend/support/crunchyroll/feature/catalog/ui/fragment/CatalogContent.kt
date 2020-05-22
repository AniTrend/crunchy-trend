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

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.ui.fragment.list.CrunchyFragmentList
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.feature.catalog.R
import co.anitrend.support.crunchyroll.feature.catalog.koin.moduleHelper
import co.anitrend.support.crunchyroll.feature.catalog.ui.adpater.CatalogAdapter
import co.anitrend.support.crunchyroll.feature.catalog.viewmodel.CatalogViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatalogContent(
    override val defaultSpanSize: Int = R.integer.single_list_size
) : CrunchyFragmentList<CrunchyCatalogWithSeries>() {

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
            CatalogAdapter(
                scope = lifecycleScope,
                resources = resources,
                stateConfiguration = stateConfig
            )
    }

    private val viewModel by viewModel<CatalogViewModel>()

    /**
     * Invoke view model observer to watch for changes
     */
    @ExperimentalCoroutinesApi
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(viewLifecycleOwner, Observer { model ->
            onPostModelChange(model)
        })
    }

    /**
     * Expects a module helper if one is available for the current scope, otherwise return null
     */
    override fun featureModuleHelper() = moduleHelper

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state

    /**
     * Stub to trigger the loading of data, by default this is only called
     * when [supportViewAdapter] has no data in its underlying source.
     *
     * This is called when the fragment reaches it's [onStart] state
     *
     * @see initializeComponents
     */
    override fun onFetchDataInitialize() {
        viewModel.state()
    }

    /**
     * State configuration for any underlying state representing widgets
     */
    override val stateConfig: StateLayoutConfig by inject()
}