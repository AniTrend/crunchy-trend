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

package co.anitrend.support.crunchyroll.feature.media.ui.fragment

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.domain.entities.LoadState
import co.anitrend.arch.domain.entities.RequestError
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.extension.ext.argument
import co.anitrend.arch.recycler.common.ClickableItem
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.core.extensions.createDialog
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.ui.fragment.list.CrunchyFragmentList
import co.anitrend.support.crunchyroll.domain.media.entities.CrunchyMedia
import co.anitrend.support.crunchyroll.domain.media.models.CrunchyMediaQuery
import co.anitrend.support.crunchyroll.feature.media.R
import co.anitrend.support.crunchyroll.feature.media.databinding.DialogMediaBinding
import co.anitrend.support.crunchyroll.feature.media.presenter.MediaPresenter
import co.anitrend.support.crunchyroll.feature.media.ui.adapter.MediaAdapter
import co.anitrend.support.crunchyroll.feature.media.viewmodel.MediaViewModel
import co.anitrend.support.crunchyroll.navigation.*
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaContent(
    override val defaultSpanSize: Int = R.integer.single_list_size
) : CrunchyFragmentList<CrunchyMedia>() {

    private val payload
            by argument<Media.Payload>(
                Media.extraKey
            )

    private val viewModel by viewModel<MediaViewModel>()

    override val stateConfig: StateLayoutConfig by inject()

    override val supportViewAdapter by lazy(UNSAFE) {
        MediaAdapter(
            resources = resources,
            stateConfiguration = stateConfig
        )
    }

    override fun setUpViewModelObserver() {
        viewModelState().model.observe(
            viewLifecycleOwner,
            Observer {
                onPostModelChange(it)
            }
        )
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
            supportViewAdapter.clickableFlow.debounce(DEBOUNCE_DURATION)
                .filterIsInstance<ClickableItem.Data<CrunchyMedia>>()
                .collect {
                    val media = it.data
                    activity?.createDialog(BottomSheet(LayoutMode.WRAP_CONTENT))
                        ?.setPeekHeight(res = R.dimen.design_nav_header_height)
                        ?.cornerRadius(res = R.dimen.xl_margin)
                        ?.customView(
                            viewRes = R.layout.dialog_media,
                            horizontalPadding = false,
                            noVerticalPadding = true,
                            dialogWrapContent = true
                        )
                        ?.show {
                            val binding = DialogMediaBinding.bind(getCustomView())
                            binding.dialogMediaDuration.text = MediaPresenter.durationFormatted(media.duration)
                            binding.dialogMediaTitle.text = media.name
                            binding.dialogMediaDescription.text = media.description
                            binding.dialogMediaImage.setImageUrl(media.screenshotImage)
                            binding.dialogMediaImageContainer.setOnClickListener {
                                val mediaPlayerPayload = MediaPlayer.Payload(
                                    mediaId = media.mediaId,
                                    collectionName = payload?.collectionName,
                                    collectionThumbnail = payload?.collectionThumbnail,
                                    episodeTitle = "Episode ${media.episodeNumber}: ${media.name}",
                                    episodeThumbnail = media.screenshotImage
                                )
                                MediaPlayer(
                                    binding.root.context, mediaPlayerPayload
                                )
                            }
                            binding.dialogMediaDownload.setOnClickListener {
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

    override fun onFetchDataInitialize() {
        val mediaPayload = payload
        if (mediaPayload != null) {
            viewModel.state(
                CrunchyMediaQuery(
                    collectionId = mediaPayload.collectionId
                )
            )
        } else {
            listPresenter.stateLayout.loadStateFlow.value =
                LoadState.Error(
                    RequestError(
                        topic = "Invalid fragment parameters ${Emote.Cry}",
                        description = "Invalid or missing payload, request cannot be processed"
                    )
                )
        }
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state
}