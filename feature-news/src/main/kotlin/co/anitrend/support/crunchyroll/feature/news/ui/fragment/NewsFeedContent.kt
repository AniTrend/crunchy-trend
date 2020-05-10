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

package co.anitrend.support.crunchyroll.feature.news.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.startNewActivity
import co.anitrend.arch.recycler.common.DefaultClickableItem
import co.anitrend.arch.ui.fragment.paged.SupportFragmentPagedList
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.extensions.toBundle
import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.paged.CrunchyFragmentPaged
import co.anitrend.support.crunchyroll.data.arch.extension.toCrunchyLocale
import co.anitrend.support.crunchyroll.data.locale.helper.ICrunchySessionLocale
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews
import co.anitrend.support.crunchyroll.feature.news.R
import co.anitrend.support.crunchyroll.feature.news.koin.moduleHelper
import co.anitrend.support.crunchyroll.feature.news.ui.activity.NewsScreen
import co.anitrend.support.crunchyroll.feature.news.ui.adapter.RssNewsAdapter
import co.anitrend.support.crunchyroll.feature.news.viewmodel.NewsViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewsFeedContent(
    override val defaultSpanSize: Int = R.integer.single_list_size
) : CrunchyFragmentPaged<CrunchyNews>() {

    val viewModel by viewModel<NewsViewModel>()

    override val stateConfig: StateLayoutConfig by inject()

    /**
     * Expects a module helper if one is available for the current scope, otherwise return null
     */
    override fun featureModuleHelper() = moduleHelper

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
        RssNewsAdapter(
            resources = resources,
            stateConfiguration = stateConfig,
            markwon = get()
        )
    }

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(viewLifecycleOwner, Observer {
            onPostModelChange(it)
        })
    }

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
                .filterIsInstance<DefaultClickableItem<CrunchyNews>>()
                .collect {
                    val model = it.data
                    if (model != null) {
                        val payload = NavigationTargets.News.Payload(
                            model.title,
                            model.subTitle,
                            model.description,
                            model.content,
                            model.publishedOn
                        ).toBundle(NavigationTargets.News.PAYLOAD)

                        it.view.context.startNewActivity<NewsScreen>(payload)
                    }
                }
        }
    }


    override fun onFetchDataInitialize() {
        val currentLocale = get<ICrunchySessionLocale>().sessionLocale
        viewModel.state(
            RssQuery(
                language = currentLocale.toCrunchyLocale()
            )
        )
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
        supportRecyclerView?.adapter = null
        super.onDestroyView()
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state
}