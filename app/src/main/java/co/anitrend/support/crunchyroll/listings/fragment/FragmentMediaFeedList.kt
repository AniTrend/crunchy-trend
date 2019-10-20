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

package co.anitrend.support.crunchyroll.listings.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import co.anitrend.arch.core.viewmodel.SupportPagingViewModel
import co.anitrend.arch.extension.startNewActivity
import co.anitrend.arch.ui.fragment.SupportFragmentPagedList
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.viewmodel.MediaListingViewModel
import co.anitrend.support.crunchyroll.data.extension.getCrunchyLocale
import co.anitrend.support.crunchyroll.domain.entities.query.rss.RssQuery
import co.anitrend.support.crunchyroll.domain.entities.result.rss.MediaListing
import co.anitrend.support.crunchyroll.listings.adapter.RssMediaAdapter
import co.anitrend.support.crunchyroll.listings.presenter.ListingPresenter
import co.anitrend.support.crunchyroll.stream.activity.StreamingActivity
import co.anitrend.support.crunchyroll.stream.fragment.FragmentMediaStream
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentMediaFeedList : SupportFragmentPagedList<MediaListing, CrunchyCorePresenter, PagedList<MediaListing>>() {

    override val supportStateConfiguration = SupportStateLayoutConfiguration(
        loadingDrawable = R.drawable.ic_crunchyroll,
        errorDrawable = R.drawable.ic_support_empty_state,
        loadingMessage = R.string.Loading,
        retryAction = R.string.action_retry
    )

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<ListingPresenter>()

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return view model of the given type
     */
    override val supportViewModel by viewModel<MediaListingViewModel>()

    override val supportViewAdapter =
        RssMediaAdapter(
            presenter = supportPresenter,
            clickListener = object : ItemClickListener<MediaListing> {
                override fun onItemClick(target: View, data: Pair<Int, MediaListing?>) {
                    target.context.startNewActivity<StreamingActivity>(
                        Bundle().apply {
                            val payload = FragmentMediaStream.Companion.Payload(
                                mediaId = data.second?.id ?: 0,
                                episodeThumbnail = data.second?.episodeThumbnail
                            )
                            putParcelable(FragmentMediaStream.PAYLOAD, payload)
                        }
                    )
                }

                override fun onItemLongClick(target: View, data: Pair<Int, MediaListing?>) {

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
     * Handles the complex logic required to dispatch network request to [SupportPagingViewModel]
     * which uses [SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportPagingViewModel.model]
     *
     * @see [SupportPagingViewModel.requestBundleLiveData]
     */
    override fun onFetchDataInitialize() {
        supportViewModel(
            RssQuery(
                language = getCrunchyLocale()
            )
        )
    }

    override val columnSize: Int = R.integer.single_list_size

    companion object {
        const val PAYLOAD = "FragmentMediaFeedList:Payload"

        fun newInstance(): FragmentMediaFeedList {
            return FragmentMediaFeedList()
        }
    }
}