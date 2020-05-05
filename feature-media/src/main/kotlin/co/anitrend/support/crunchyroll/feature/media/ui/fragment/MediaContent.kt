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
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.argument
import co.anitrend.arch.ui.fragment.paged.SupportFragmentPagedList
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.core.extensions.createDialog
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.IFragmentFactory
import co.anitrend.support.crunchyroll.domain.media.entities.CrunchyMedia
import co.anitrend.support.crunchyroll.domain.media.models.CrunchyMediaQuery
import co.anitrend.support.crunchyroll.feature.media.R
import co.anitrend.support.crunchyroll.feature.media.presenter.MediaPresenter
import co.anitrend.support.crunchyroll.feature.media.ui.adapter.MediaAdapter
import co.anitrend.support.crunchyroll.feature.media.viewmodel.MediaViewModel
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import kotlinx.android.synthetic.main.dialog_media.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaContent(
    override val columnSize: Int = R.integer.single_list_size
) : SupportFragmentPagedList<CrunchyMedia>() {

    private val payload
            by argument<NavigationTargets.Media.Payload>(
                NavigationTargets.Media.PAYLOAD
            )

    private val presenter by inject<MediaPresenter>()

    private val viewModel by viewModel<MediaViewModel>()

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
        MediaAdapter(
            presenter = presenter,
            stateConfig = stateConfig,
            itemClickListener = object : ItemClickListener<CrunchyMedia> {

                override fun onItemClick(target: View, data: Pair<Int, CrunchyMedia?>) {
                    val media = data.second
                    if (media != null) {
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
                                view.dialog_media_duration.text = presenter.durationFormatted(media.duration)
                                view.dialog_media_title.text = media.name
                                view.dialog_media_description.text = media.description
                                view.dialog_media_image.setImageUrl(media.screenshotImage)
                                view.dialog_media_image_container.setOnClickListener {
                                    val mediaPlayerPayload = NavigationTargets.MediaPlayer.Payload(
                                        mediaId = media.mediaId,
                                        collectionName = payload?.collectionName,
                                        collectionThumbnail = payload?.collectionThumbnail,
                                        episodeTitle = "Episode ${media.episodeNumber}: ${media.name}",
                                        episodeThumbnail = media.screenshotImage
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

                override fun onItemLongClick(target: View, data: Pair<Int, CrunchyMedia?>) {

                }
            }
        )
    }

    override fun setUpViewModelObserver() {
        viewModelState().model.observe(viewLifecycleOwner, Observer {
            onPostModelChange(it)
        })
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {

    }

    override fun onUpdateUserInterface() {

    }

    override fun onFetchDataInitialize() {
        payload?.also {
            viewModel.state(
                CrunchyMediaQuery(
                    collectionId = it.collectionId
                )
            )
        } ?: supportStateLayout?.setNetworkState(
            NetworkState.Error(
                heading = "Invalid fragment parameters ${Emote.Cry}",
                message = "Invalid or missing payload, request cannot be processed"
            )
        )
    }

    override val stateConfig by inject<StateLayoutConfig>()

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

    companion object : IFragmentFactory<MediaContent> {
        override val fragmentTag = MediaContent::class.java.simpleName

        override fun newInstance(bundle: Bundle?) =
            MediaContent().apply {
                arguments = bundle
            }
    }
}