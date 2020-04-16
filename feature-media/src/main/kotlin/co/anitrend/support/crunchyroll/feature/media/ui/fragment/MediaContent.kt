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
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.argument
import co.anitrend.arch.ui.fragment.SupportFragmentPagedList
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.IFragmentFactory
import co.anitrend.support.crunchyroll.domain.media.entities.CrunchyMedia
import co.anitrend.support.crunchyroll.domain.media.models.CrunchyMediaQuery
import co.anitrend.support.crunchyroll.feature.media.R
import co.anitrend.support.crunchyroll.feature.media.presenter.MediaPresenter
import co.anitrend.support.crunchyroll.feature.media.ui.adapter.MediaAdapter
import co.anitrend.support.crunchyroll.feature.media.viewmodel.MediaViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaContent : SupportFragmentPagedList<CrunchyMedia, MediaPresenter, PagedList<CrunchyMedia>>() {

    private val payload
            by argument<NavigationTargets.Media.Payload>(
                NavigationTargets.Media.PAYLOAD
            )

    override val supportPresenter by inject<MediaPresenter>()

    override val supportViewModel by viewModel<MediaViewModel>()

    override val supportViewAdapter by lazy(LAZY_MODE_UNSAFE) {
        MediaAdapter(
            presenter = supportPresenter,
            stateConfiguration = supportStateConfiguration,
            itemClickListener = object : ItemClickListener<CrunchyMedia> {

                override fun onItemClick(target: View, data: Pair<Int, CrunchyMedia?>) {
                    val media = data.second
                    val mediaPlayerPayload = NavigationTargets.MediaPlayer.Payload(
                        mediaId = media?.mediaId ?: 0,
                        collectionName = payload?.collectionName,
                        collectionThumbnail = payload?.collectionThumbnail,
                        episodeTitle = "Episode ${media?.episodeNumber}: ${media?.name}",
                        episodeThumbnail = media?.screenshotImage
                    )
                    NavigationTargets.MediaPlayer(
                        target.context, mediaPlayerPayload
                    )
                }

                override fun onItemLongClick(target: View, data: Pair<Int, CrunchyMedia?>) {

                }
            }
        )
    }

    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(viewLifecycleOwner, Observer {
            onPostModelChange(it)
        })
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {

    }

    override fun onUpdateUserInterface() {

    }

    override fun onFetchDataInitialize() {
        payload?.also {
            supportViewModel(
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

    override val supportStateConfiguration by inject<SupportStateLayoutConfiguration>()

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

    companion object : IFragmentFactory<MediaContent> {
        override val FRAGMENT_TAG = MediaContent::class.java.simpleName

        override fun newInstance(bundle: Bundle?) =
            MediaContent().apply {
                arguments = bundle
            }
    }
}