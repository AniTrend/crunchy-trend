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

package co.anitrend.support.crunchyroll.feature.listing.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import androidx.lifecycle.Observer
import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.core.viewmodel.SupportPagingViewModel
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.ui.fragment.paged.SupportFragmentPagedList
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.core.extensions.createDialog
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.IFragmentFactory
import co.anitrend.support.crunchyroll.data.arch.extension.toCrunchyLocale
import co.anitrend.support.crunchyroll.data.locale.helper.ICrunchySessionLocale
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.episode.entities.CrunchyEpisodeFeed
import co.anitrend.support.crunchyroll.feature.feed.R
import co.anitrend.support.crunchyroll.feature.listing.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.listing.presenter.ListingPresenter
import co.anitrend.support.crunchyroll.feature.listing.ui.adapter.RssMediaAdapter
import co.anitrend.support.crunchyroll.feature.listing.viewmodel.MediaListingViewModel
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.dialog_media.view.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFeedContent(
    override val columnSize: Int = R.integer.single_list_size
) : SupportFragmentPagedList<CrunchyEpisodeFeed>() {

    override val stateConfig by inject<StateLayoutConfig>()

    private val viewModel by viewModel<MediaListingViewModel>()

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
        RssMediaAdapter(
            stateConfig,
            object : ItemClickListener<CrunchyEpisodeFeed> {
                override fun onItemClick(target: View, data: Pair<Int, CrunchyEpisodeFeed?>) {
                    val episodeFeed = data.second
                    if (episodeFeed != null) {
                        activity?.createDialog(BottomSheet(LayoutMode.WRAP_CONTENT))
                            ?.setPeekHeight(res = R.dimen.app_bar_height)
                            ?.cornerRadius(res = R.dimen.xl_margin)
                            ?.customView(
                                viewRes = R.layout.dialog_media,
                                horizontalPadding = false,
                                noVerticalPadding = true,
                                dialogWrapContent = true
                            )
                            ?.show {
                                val view = getCustomView()
                                view.dialog_media_duration.text = episodeFeed.episodeDuration
                                view.dialog_media_title.text = episodeFeed.episodeTitle
                                view.dialog_media_description.text = episodeFeed.description?.parseAsHtml(
                                    flags = HtmlCompat.FROM_HTML_MODE_COMPACT
                                )
                                view.dialog_media_image.setImageUrl(episodeFeed.episodeThumbnail)
                                view.dialog_media_image_container.setOnClickListener {
                                    val mediaPlayerPayload = NavigationTargets.MediaPlayer.Payload(
                                        mediaId = episodeFeed.id,
                                        collectionName = episodeFeed.title,
                                        collectionThumbnail = null,
                                        episodeTitle = "Episode ${episodeFeed.episodeNumber}: ${episodeFeed.episodeTitle}",
                                        episodeThumbnail = episodeFeed.episodeThumbnail
                                    )
                                    NavigationTargets.MediaPlayer(
                                        target.context, mediaPlayerPayload
                                    )
                                }
                                view.dialog_media_download.setOnClickListener {
                                    // TODO: Move download to the player screen?
                                    Toast.makeText(
                                        it.context,
                                        "Not yet implemented.. ${Emote.Heart}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                // possible regression https://github.com/afollestad/material-dialogs/issues/1925
                                /*cancelable(false)
                            cancelOnTouchOutside(false)
                            noAutoDismiss()*/
                            }
                    }
                }

                override fun onItemLongClick(target: View, data: Pair<Int, CrunchyEpisodeFeed?>) {

                }
            }
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
     * Handles the complex logic required to dispatch network request to [SupportPagingViewModel]
     * which uses [SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportPagingViewModel.model]
     *
     * @see [SupportPagingViewModel.requestBundleLiveData]
     */
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

    companion object : IFragmentFactory<MediaFeedContent> {
        override val fragmentTag = MediaFeedContent::class.java.simpleName

        override fun newInstance(bundle: Bundle?) = MediaFeedContent()
    }
}