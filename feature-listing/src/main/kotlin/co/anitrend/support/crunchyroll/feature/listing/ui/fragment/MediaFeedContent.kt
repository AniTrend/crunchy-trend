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
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.extension.ext.gone
import co.anitrend.arch.recycler.common.DefaultClickableItem
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.core.extensions.createDialog
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.navigation.*
import co.anitrend.support.crunchyroll.core.ui.fragment.list.CrunchyFragmentList
import co.anitrend.support.crunchyroll.data.arch.extension.toCrunchyLocale
import co.anitrend.support.crunchyroll.data.locale.helper.ICrunchySessionLocale
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.episode.entities.CrunchyEpisodeFeed
import co.anitrend.support.crunchyroll.feature.listing.databinding.DialogMediaBinding
import co.anitrend.support.crunchyroll.feature.listing.R
import co.anitrend.support.crunchyroll.feature.listing.ui.adapter.RssMediaAdapter
import co.anitrend.support.crunchyroll.feature.listing.viewmodel.MediaListingViewModel
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFeedContent(
    override val defaultSpanSize: Int = R.integer.single_list_size
) : CrunchyFragmentList<CrunchyEpisodeFeed>() {

    private val viewModel by viewModel<MediaListingViewModel>()

    override val stateConfig: StateLayoutConfig by inject()

    override val supportViewAdapter by lazy(UNSAFE) {
        RssMediaAdapter(
            resources = resources,
            stateConfiguration = stateConfig
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
    override fun initializeComponents(savedInstanceState: Bundle?) {
        super.initializeComponents(savedInstanceState)
        lifecycleScope.launchWhenResumed {
            supportViewAdapter.clickableStateFlow.debounce(DEBOUNCE_DURATION)
                .filterIsInstance<DefaultClickableItem<CrunchyEpisodeFeed>>()
                .collect {
                    val episodeFeed = it.data
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
                                val binding = DialogMediaBinding.bind(getCustomView())

                                binding.dialogMediaDuration.text = episodeFeed.episodeDuration
                                binding.dialogMediaTitle.text = episodeFeed.episodeTitle
                                binding.dialogMediaDescription.gone()
                                /*view.dialog_media_description.text = episodeFeed.description?.parseAsHtml(
                                    flags = HtmlCompat.FROM_HTML_MODE_COMPACT
                                )*/
                                binding.dialogMediaImage.setImageUrl(episodeFeed.episodeThumbnail)
                                binding.dialogMediaImageContainer.setOnClickListener { _ ->
                                    val mediaPlayerPayload = MediaPlayer.Payload(
                                        mediaId = episodeFeed.id,
                                        collectionName = episodeFeed.title,
                                        collectionThumbnail = null,
                                        episodeTitle = "Episode ${episodeFeed.episodeNumber}: ${episodeFeed.episodeTitle}",
                                        episodeThumbnail = episodeFeed.episodeThumbnail
                                    )
                                    MediaPlayer(
                                        binding.root.context, mediaPlayerPayload
                                    )
                                }
                                binding.dialogMediaDownload.setOnClickListener { _ ->
                                    // TODO: Move download to the player screen?
                                    Toast.makeText(
                                        binding.root.context,
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
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state
}