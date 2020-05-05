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

package co.anitrend.support.crunchyroll.feature.player.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.*
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.extensions.createDialog
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.model.UserAgent
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.IFragmentFactory
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.domain.stream.models.CrunchyMediaStreamQuery
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.component.SourceFactoryProvider
import co.anitrend.support.crunchyroll.feature.player.model.track.contract.IMediaTrack
import co.anitrend.support.crunchyroll.feature.player.plugin.MediaPluginImpl
import co.anitrend.support.crunchyroll.feature.player.plugin.PlaylistManagerPluginImpl
import co.anitrend.support.crunchyroll.feature.player.presenter.StreamPresenter
import co.anitrend.support.crunchyroll.feature.player.viewmodel.MediaStreamViewModel
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.devbrackets.android.exomedia.listener.VideoControlsSeekListener
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener
import com.devbrackets.android.exomedia.ui.widget.VideoControls
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_media_player.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MediaStreamContent(
    override var inflateLayout: Int = R.layout.fragment_media_player
) : SupportFragment<MediaStream?>() {

    private val qualityButton by lazy(LAZY_MODE_UNSAFE) {
        AppCompatImageButton(context).apply {
            setBackgroundResource(android.R.color.transparent)
            setImageResource(R.drawable.ic_hd_white_24dp)
            val pixelSize = resources.getDimensionPixelSize(R.dimen.xl_margin)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(pixelSize, 0, 0, 0)  }
            isClickable = true
            isFocusable = true
            background = context.getDrawableAttr(android.R.attr.selectableItemBackground)
            setOnClickListener { showVideoTracksMenu() }
        }
    }
    private val audioButton by lazy(LAZY_MODE_UNSAFE) {
        AppCompatImageButton(context).apply {
            setBackgroundResource(android.R.color.transparent)
            setImageResource(R.drawable.ic_audiotrack_white_24dp)
            val pixelSize = resources.getDimensionPixelSize(R.dimen.xl_margin)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(pixelSize, 0, 0, 0) }
            isClickable = true
            isFocusable = true
            background = context.getDrawableAttr(android.R.attr.selectableItemBackground)
            setOnClickListener { showAudioMenu() }
            gone()
        }
    }
    private val captionButton by lazy(LAZY_MODE_UNSAFE) {
        AppCompatImageButton(context).apply {
            setBackgroundResource(android.R.color.transparent)
            setImageResource(R.drawable.ic_closed_caption_white_24dp)
            val pixelSize = resources.getDimensionPixelSize(R.dimen.xl_margin)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(pixelSize, 0, 0, 0)  }
            isClickable = true
            isFocusable = true
            background = context.getDrawableAttr(android.R.attr.selectableItemBackground)
            setOnClickListener { showSubtitleMenu() }
            gone()
        }
    }

    private val mediaPlugin by lazy(LAZY_MODE_UNSAFE) {
        MediaPluginImpl(
            lifecycleScope,
            exoMediaVideoView,
            get<UserAgent>().identifier,
            DefaultBandwidthMeter.Builder(context).build(),
            sourceFactoryProvider
        )
    }

    private val payload
            by argument<NavigationTargets.MediaPlayer.Payload>(
                NavigationTargets.MediaPlayer.PAYLOAD
            )

    private val sourceFactoryProvider by inject<SourceFactoryProvider>()
    private val playlistManager by inject<PlaylistManagerPluginImpl>()

    private var controlsVisibilityListener: VideoControlsVisibilityListener? = null

    private val presenter by inject<StreamPresenter>()

    private val viewModel by viewModel<MediaStreamViewModel>()

    private fun showDialogUsing(message: Int, mediaTracks: List<IMediaTrack>?) {
        if (mediaTracks.isNullOrEmpty()) {
            Snackbar.make(exoMediaVideoView, message, Snackbar.LENGTH_SHORT).show()
            return
        }

        val selectedIndex = mediaTracks.indexOfFirst {
            it.selected
        }

        activity?.createDialog()
            ?.cornerRadius(res = R.dimen.lg_margin)
            ?.listItemsSingleChoice(
                items = mediaTracks.map { it.toString() },
                initialSelection = selectedIndex,
                selection = { _: MaterialDialog, index: Int, _: CharSequence ->
                    val selected = mediaTracks[index]
                    mediaPlugin.useMediaTrack(selected)
                }
            )?.show()
    }

    private fun showVideoTracksMenu() {
        val mediaTracks = mediaPlugin.availableVideoTracks()
        showDialogUsing(R.string.player_text_unavailable_video_tracks, mediaTracks)
    }

    private fun showSubtitleMenu() {
        val mediaTracks = mediaPlugin.availableCaptionTracks()
        showDialogUsing(R.string.player_text_unavailable_caption_tracks, mediaTracks)
    }

    private fun showAudioMenu() {
        val mediaTracks = mediaPlugin.availableAudioTracks()
        showDialogUsing(R.string.player_text_unavailable_audio_tracks, mediaTracks)
    }

    override fun setUpViewModelObserver() {
        viewModelState().model.observe(viewLifecycleOwner, Observer {
            launch {
                prepareResults(it)
            }
        })
        viewModelState().networkState.observe(viewLifecycleOwner, Observer {
            supportStateLayout.setNetworkState(it)
        })
        viewModelState().refreshState.observe(viewLifecycleOwner, Observer {
            supportStateLayout.setNetworkState(it)
        })
        supportStateLayout.interactionLiveData.observe(viewLifecycleOwner, Observer {
            viewModelState().retry()
        })
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {
        launch {
            lifecycle.whenResumed {
                if (!viewModelState().isEmpty())
                    onFetchDataInitialize()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launch { onUpdateUserInterface() }
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [co.anitrend.arch.ui.fragment.contract.ISupportFragmentList]
     * will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        supportStateLayout.stateConfig = get()
        mediaPlugin.onInitializing()
        mediaPlugin.setVisibilityListener(
            controlsVisibilityListener
        )
        (exoMediaVideoView.videoControls as? VideoControls)?.also {
            it.seekListener = object: VideoControlsSeekListener {
                override fun onSeekStarted(): Boolean {
                    playlistManager.invokeSeekStarted()
                    return true
                }

                override fun onSeekEnded(seekTime: Long): Boolean {
                    playlistManager.invokeSeekEnded(seekTime)
                    return true
                }
            }
            if (exoMediaVideoView.trackSelectionAvailable()) {
                it.addExtraView(qualityButton)
                it.addExtraView(audioButton)
                it.addExtraView(captionButton)
            }
        }
    }

    private fun prepareResults(mediaStreams: List<MediaStream>?) {
        if (!mediaStreams.isNullOrEmpty()) {
            val streams = presenter.mapToStreamItems(
                mediaStreams,
                payload
            )

            val playIndex = presenter.getAdaptiveStreamIndex(streams)
            val mediaItem = streams[playIndex]
            val playHead = mediaItem.mediaPlayHead

            //playlistManager.setParameters(streams, playIndex)
            playlistManager.apply {
                id = mediaItem.id
                setParameters(
                    listOf(mediaItem), 0
                )
                play(
                    playHead.toLong(),
                    false
                )
            }
        } else
            supportStateLayout.setNetworkState(
                NetworkState.Error(
                    heading = "No streams available ${Emote.Eyes}",
                    message = "Content may be unavailable in your country, please check with original source"
                )
            )
    }

    /**
     * Handles the complex logic required to dispatch network request to [MediaStreamViewModel]
     * which uses [co.anitrend.arch.data.repository.SupportRepository] to either request
     * from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [MediaStreamViewModel.state]
     *
     * @see [MediaStreamViewModel.state]
     */
    override fun onFetchDataInitialize() {
        payload?.also {
            viewModel.state(
                parameter = CrunchyMediaStreamQuery(
                    mediaId = it.mediaId
                )
            )
        } ?: supportStateLayout.setNetworkState(
            NetworkState.Error(
                heading = "Invalid fragment parameters ${Emote.Cry}",
                message = "Invalid or missing payload, request cannot be processed"
            )
        )
    }

    override fun onResume() {
        super.onResume()
        playlistManager.addMediaPlugin(mediaPlugin)
    }

    override fun onPause() {
        playlistManager.removeMediaPlugin(mediaPlugin)
        super.onPause()
    }

    /**
     * Called when a fragment is first attached to its context.
     * [onCreate] will be called after this.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is VideoControlsVisibilityListener)
            controlsVisibilityListener = context
        attachComponent(playlistManager)
        //attachComponent(sourceFactoryProvider)
    }

    /**
     * Called when the fragment is no longer attached to its activity.
     * This is called after [onDestroy].
     */
    override fun onDetach() {
        controlsVisibilityListener = null
        detachComponent(playlistManager)
        //detachComponent(sourceFactoryProvider)
        super.onDetach()
    }

    /**
     * Called when the fragment is no longer in use. This is called
     * after [onStop] and before [onDetach].
     */
    override fun onDestroy() {
        controlsVisibilityListener = null
        super.onDestroy()
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state

    /**
     * Listens to the system to determine when to show the default controls
     * for the [VideoView]
     */
    internal inner class FullScreenListener : View.OnSystemUiVisibilityChangeListener {
        var lastVisibility = 0
            private set

        override fun onSystemUiVisibilityChange(visibility: Int) {
            // NOTE: if the screen is double tapped in just the right way (or wrong way)
            // the SYSTEM_UI_FLAG_HIDE_NAVIGATION flag is dropped. Because of this we
            // no longer get notified of the temporary change when the screen is tapped
            // (i.e. the VideoControls get the touch event instead of the OS). So we store
            // the visibility off for use in the ControlsVisibilityListener for verification
            lastVisibility = visibility
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                exoMediaVideoView.showControls()
            }
        }
    }

    companion object : IFragmentFactory<MediaStreamContent> {

        override val fragmentTag = MediaStreamContent::class.java.simpleName

        override fun newInstance(bundle: Bundle?) =
            MediaStreamContent().apply {
                arguments = bundle
            }
    }
}