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

package co.anitrend.support.crunchyroll.ui.fragments

import android.os.Bundle
import androidx.paging.PagedList
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.viewmodel.rss.CrunchyRssNewsViewModel
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssNews
import co.anitrend.support.crunchyroll.data.usecase.rss.CrunchyRssNewsUseCase
import io.wax911.support.data.model.NetworkState
import io.wax911.support.extension.LAZY_MODE_UNSAFE
import io.wax911.support.ui.fragment.SupportFragmentList
import io.wax911.support.ui.recycler.adapter.SupportViewAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentFeedNewsList : SupportFragmentList<CrunchyRssNews, CrunchyCorePresenter, PagedList<CrunchyRssNews>>() {

    private val payload by lazy(LAZY_MODE_UNSAFE) {
        arguments?.getParcelable<CrunchyRssNewsUseCase.Payload>(PAYLOAD)
    }

    override val supportViewAdapter: SupportViewAdapter<CrunchyRssNews>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(this, this)
    }

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
    override val supportViewModel by viewModel<CrunchyRssNewsViewModel>()

    /**
     * Additional initialization to be done in this method, if the overriding class is type of
     * [androidx.fragment.app.Fragment] then this method will be called in
     * [androidx.fragment.app.FragmentActivity.onCreate]. Otherwise
     * [androidx.fragment.app.FragmentActivity.onPostCreate] invokes this function
     *
     * @param savedInstanceState
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {

    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [CompatView] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun updateUI() {

    }

    /**
     * Handles the complex logic required to dispatch network request to [SupportViewModel]
     * which uses [SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportViewModel.model]
     *
     * @see [SupportViewModel.queryFor]
     */
    override fun makeRequest() {
        val isNull = payload?.also {
            supportViewModel(
                parameter = it
            )
        } == null
        if (isNull)
            changeLayoutState(
                NetworkState.error(
                    msg = "Media category not selected"
                )
            )
    }

    /**
     * Called when the data is changed.
     * @param t  The new data
     */
    override fun onChanged(t: PagedList<CrunchyRssNews>?) {
        onPostModelChange(t)
    }

    override val retryButtonText: Int = R.string.action_retry
    override val columnSize: Int = R.integer.single_list_size

    companion object {
        private const val PAYLOAD = "FragmentFeedNewsList:Payload"

        fun newInstance(payload: CrunchyRssNewsUseCase.Payload): FragmentFeedNewsList {
            return FragmentFeedNewsList().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYLOAD, payload)
                }
            }
        }
    }
}