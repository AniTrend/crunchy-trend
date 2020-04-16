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
import androidx.paging.PagedList
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.startNewActivity
import co.anitrend.arch.ui.fragment.SupportFragmentPagedList
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.support.crunchyroll.core.extensions.toBundle
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.fragment.IFragmentFactory
import co.anitrend.support.crunchyroll.data.arch.extension.toCrunchyLocale
import co.anitrend.support.crunchyroll.data.locale.helper.ICrunchySessionLocale
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews
import co.anitrend.support.crunchyroll.feature.news.R
import co.anitrend.support.crunchyroll.feature.news.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.news.ui.activity.NewsScreen
import co.anitrend.support.crunchyroll.feature.news.ui.adapter.RssNewsAdapter
import co.anitrend.support.crunchyroll.feature.news.viewmodel.NewsViewModel
import io.noties.markwon.Markwon
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewsFeedContent : SupportFragmentPagedList<CrunchyNews, CrunchyCorePresenter, PagedList<CrunchyNews>>() {

    private val markwon by inject<Markwon>()

    override val supportStateConfiguration
            by inject<SupportStateLayoutConfiguration>()

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<CrunchyCorePresenter>()

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return view model of the given type
     */
    override val supportViewModel by viewModel<NewsViewModel>()

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
        RssNewsAdapter(
            markwon,
            supportStateConfiguration,
            object : ItemClickListener<CrunchyNews> {

                override fun onItemClick(target: View, data: Pair<Int, CrunchyNews?>) {
                    val payload = NavigationTargets.News.Payload(
                        data.second?.title,
                        data.second?.subTitle,
                        data.second?.description,
                        data.second?.content
                    ).toBundle(NavigationTargets.News.PAYLOAD)

                    target.context.startNewActivity<NewsScreen>(payload)
                }

                override fun onItemLongClick(target: View, data: Pair<Int, CrunchyNews?>) {

                }
            }
        )
    }

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(viewLifecycleOwner, Observer {
            onPostModelChange(it)
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
     * Handles the complex logic required to dispatch network request to [SupportViewModel]
     * which uses [SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportViewModel.model]
     *
     * @see [SupportViewModel.requestBundleLiveData]
     */
    override fun onFetchDataInitialize() {
        val currentLocale = get<ICrunchySessionLocale>().sessionLocale
        supportViewModel(
            RssQuery(
                language = currentLocale.toCrunchyLocale()
            )
        )
    }

    override val columnSize: Int = R.integer.single_list_size

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

    companion object : IFragmentFactory<NewsFeedContent> {
        override val FRAGMENT_TAG = NewsFeedContent::class.java.simpleName

        override fun newInstance(bundle: Bundle?) = NewsFeedContent()
    }
}