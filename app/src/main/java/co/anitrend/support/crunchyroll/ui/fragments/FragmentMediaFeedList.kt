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
import android.view.View
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.viewmodel.rss.CrunchyRssMediaViewModel
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssMedia
import co.anitrend.support.crunchyroll.data.usecase.rss.CrunchyRssMediaUseCase
import co.anitrend.support.crunchyroll.ui.adapters.RssMediaAdapter
import io.wax911.support.core.viewmodel.SupportViewModel
import io.wax911.support.data.model.NetworkState
import io.wax911.support.extension.LAZY_MODE_UNSAFE
import io.wax911.support.ui.fragment.SupportFragmentList
import io.wax911.support.ui.recycler.adapter.SupportViewAdapter
import io.wax911.support.ui.recycler.holder.event.ItemClickListener
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentMediaFeedList : SupportFragmentList<CrunchyRssMedia, CrunchyCorePresenter, PagedList<CrunchyRssMedia>>() {

    private val payload by lazy(LAZY_MODE_UNSAFE) {
        arguments?.getParcelable<CrunchyRssMediaUseCase.Payload>(PAYLOAD)
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
    override val supportViewModel by viewModel<CrunchyRssMediaViewModel>()

    override val supportViewAdapter = RssMediaAdapter(
        presenter = supportPresenter,
        clickListener = object: ItemClickListener<CrunchyRssMedia> {
            override fun onItemClick(target: View, data: Pair<Int, CrunchyRssMedia?>) {

            }

            override fun onItemLongClick(target: View, data: Pair<Int, CrunchyRssMedia?>) {

            }
        }
    )

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(this, Observer {
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

    override val retryButtonText: Int = R.string.action_retry
    override val columnSize: Int = R.integer.single_list_size

    companion object {
        private const val PAYLOAD = "FragmentMediaFeedList:Payload"

        fun newInstance(payload: CrunchyRssMediaUseCase.Payload): FragmentMediaFeedList {
            return FragmentMediaFeedList().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYLOAD, payload)
                }
            }
        }
    }
}