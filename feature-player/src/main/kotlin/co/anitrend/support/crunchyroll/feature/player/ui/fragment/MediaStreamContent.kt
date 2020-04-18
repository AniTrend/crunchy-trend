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
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.*
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.extensions.createDialog
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.fragment.IFragmentFactory
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.domain.stream.models.CrunchyMediaStreamQuery
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.component.SourceFactoryProvider
import co.anitrend.support.crunchyroll.feature.player.model.MediaStreamItem
import co.anitrend.support.crunchyroll.feature.player.model.MediaStreamWithExtras
import co.anitrend.support.crunchyroll.core.model.UserAgent
import co.anitrend.support.crunchyroll.feature.player.model.MediaTrack
import co.anitrend.support.crunchyroll.feature.player.plugin.MediaPluginImpl
import co.anitrend.support.crunchyroll.feature.player.plugin.PlaylistManagerPlugin
import co.anitrend.support.crunchyroll.feature.player.presenter.StreamPresenter
import co.anitrend.support.crunchyroll.feature.player.viewmodel.MediaStreamViewModel
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.devbrackets.android.exomedia.ExoMedia
import com.devbrackets.android.exomedia.listener.VideoControlsSeekListener
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener
import com.devbrackets.android.exomedia.ui.widget.VideoControls
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_media_player.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.ArrayList


class MediaStreamContent(
    override var inflateLayout: Int = R.layout.fragment_media_player
) : SupportFragment<MediaStream?, CrunchyCorePresenter, List<MediaStream>?>(),
    VideoControlsSeekListener {

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
            setOnClickListener { showQualityMenu() }
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
        }
    }

    private val mediaPlugin by lazy(LAZY_MODE_UNSAFE) {
        MediaPluginImpl(
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
    private val playlistManager by inject<PlaylistManagerPlugin>()

    private var controlsVisibilityListener: VideoControlsVisibilityListener? = null

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<StreamPresenter>()

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return view model of the given type
     */
    override val supportViewModel by viewModel<MediaStreamViewModel>()

    private fun showQualityMenu() {
        val renderType = ExoMedia.RendererType.VIDEO
        val videoTrackGroupArray =
            exoMediaVideoView.availableTracks?.get(renderType) ?: return
        if (videoTrackGroupArray.isEmpty) {
            Snackbar.make(
                exoMediaVideoView,
                R.string.player_text_unavailable_video_tracks,
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val selectableTracks = ArrayList<MediaTrack>(5)
        var selectedTrack: MediaTrack? = null

        for (groupIndex in 0 until videoTrackGroupArray.length) {
            val selectedIndex = exoMediaVideoView.getSelectedTrackIndex(renderType, groupIndex)
            Timber.tag(moduleTag).d(
                "Quality selected track: $groupIndex | $selectedIndex"
            )
            val trackGroup = videoTrackGroupArray.get(groupIndex)
            for (index in 0 until trackGroup.length) {
                val format = trackGroup.getFormat(index)

                // Skip over non video formats.
                if (!format.sampleMimeType!!.startsWith("video"))
                    continue

                val title = "${format.height}p ${StreamPresenter.separator} ${supportPresenter.calculateBitRate(format.bitrate)}Mbps"
                val mediaTrack = MediaTrack(format.bitrate, title, index, groupIndex)
                if (index == selectedIndex)
                    selectedTrack = mediaTrack

                if (!selectableTracks.contains(mediaTrack))
                    selectableTracks.add(mediaTrack)
            }
        }

        selectableTracks.sortByDescending { it.id }
        val selectedIndex = selectableTracks.indexOf(selectedTrack)

        activity?.createDialog(BottomSheet(LayoutMode.WRAP_CONTENT))
            ?.cornerRadius(res = R.dimen.xl_margin)
            ?.listItemsSingleChoice(
                items = selectableTracks.map { it.title },
                initialSelection = selectedIndex,
                selection = { _: MaterialDialog, index: Int, _: CharSequence ->
                    val selected = selectableTracks[index]
                    exoMediaVideoView.setTrack(
                        renderType,
                        selected.groupIndex,
                        selected.trackIndex
                    )
                }
            )?.show()
    }

    private fun showSubtitleMenu() {
        val renderType = ExoMedia.RendererType.CLOSED_CAPTION
        val captionTrackGroupArray =
            exoMediaVideoView.availableTracks?.get(renderType) ?: return
        if (captionTrackGroupArray.isEmpty) {
            Snackbar.make(
                exoMediaVideoView,
                R.string.player_text_unavailable_caption_tracks,
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        for (groupIndex in 0 until captionTrackGroupArray.length) {
            val selectedIndex = exoMediaVideoView.getSelectedTrackIndex(renderType, groupIndex)
            Timber.tag(moduleTag).d(
                "Captions Selected Caption Track: $groupIndex | $selectedIndex"
            )
            val trackGroup = captionTrackGroupArray.get(groupIndex)
            for (index in 0 until trackGroup.length) {
                val format = trackGroup.getFormat(index)

                // Skip over non text formats.
                if (!format.sampleMimeType!!.startsWith("text"))
                    continue

            }
        }
    }

    private fun showAudioMenu() {
        val renderType = ExoMedia.RendererType.AUDIO
        val audioTrackGroupArray =
            exoMediaVideoView.availableTracks?.get(renderType) ?: return
        if (audioTrackGroupArray.isEmpty) {
            Snackbar.make(
                exoMediaVideoView,
                R.string.player_text_unavailable_audio_tracks,
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        for (groupIndex in 0 until audioTrackGroupArray.length) {
            val selectedIndex = exoMediaVideoView.getSelectedTrackIndex(renderType, groupIndex)
            Timber.tag(moduleTag).d(
                "Audio selected track: $groupIndex | $selectedIndex"
            )
            val trackGroup = audioTrackGroupArray.get(groupIndex)
            for (index in 0 until trackGroup.length) {
                val format = trackGroup.getFormat(index)

                // Skip over non audio formats.
                if (!format.sampleMimeType!!.startsWith("audio"))
                    continue

            }
        }
    }

    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(viewLifecycleOwner, Observer {
            launch { prepareResults(it) }
        })
        supportViewModel.networkState?.observe(viewLifecycleOwner, Observer {
            supportStateLayout.setNetworkState(it)
        })
        supportViewModel.refreshState?.observe(viewLifecycleOwner, Observer {
            supportStateLayout.setNetworkState(it)
        })
        supportStateLayout.interactionLiveData.observe(viewLifecycleOwner, Observer {
            supportViewModel.retry()
        })
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {
        launch {
            lifecycle.whenStarted {
                setUpViewModelObserver()
            }
            lifecycle.whenResumed {
                if (!supportViewModel.hasModelData())
                    onFetchDataInitialize()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportStateLayout.stateConfiguration = get()
        launch {
            exoMediaVideoView.handleAudioFocus = false
            exoMediaVideoView.setAnalyticsListener(
                EventLogger(null)
            )
            mediaPlugin.setVisibilityListener(
                controlsVisibilityListener
            )
            if (exoMediaVideoView.trackSelectionAvailable())
                (exoMediaVideoView.videoControls as? VideoControls)?.let {
                    it.seekListener = this@MediaStreamContent
                    if (exoMediaVideoView.trackSelectionAvailable()) {
                        it.addExtraView(qualityButton)
                        it.addExtraView(audioButton)
                        it.addExtraView(captionButton)
                    }
                }
        }
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [co.anitrend.arch.ui.fragment.contract.ISupportFragmentList]
     * will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        supportStateLayout.setNetworkState(
            NetworkState.Error(
                heading = "No streams available ${Emote.Eyes}",
                message = "Content may be unavailable in your country, please check with original source"
            )
        )
    }

    private fun prepareResults(mediaStreams: List<MediaStream>?) {
        if (!mediaStreams.isNullOrEmpty()) {
            val streams = mediaStreams.map {
                MediaStreamItem.transform(
                    MediaStreamWithExtras(
                        mediaTitle = payload?.episodeTitle,
                        mediaSubTitle = payload?.collectionName,
                        mediaArtWorkThumbnail = payload?.collectionThumbnail,
                        mediaThumbnail = payload?.episodeThumbnail,
                        mediaStream = it
                    )
                )
            }
            val optimalIndex = supportPresenter.getOptimalStreamIndex(streams)
            val mediaItem = streams[optimalIndex]
            val playHead = mediaItem.mediaPlayHead

            //playlistManager.setParameters(streams, optimalIndex)
            playlistManager.setParameters(
                listOf(mediaItem), 0
            )
            playlistManager.id = PLAYLIST_ID
            playlistManager.play(
                playHead.toLong(),
                false
            )
        } else
            onUpdateUserInterface()
    }

    /**
     * Handles the complex logic required to dispatch network request to [MediaStreamViewModel]
     * which uses [co.anitrend.arch.data.repository.SupportRepository] to either request
     * from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [MediaStreamViewModel.model]
     *
     * @see [MediaStreamViewModel.invoke]
     */
    override fun onFetchDataInitialize() {
        payload?.also {
            supportViewModel(
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
     * Called when the fragment is no longer in use.  This is called
     * after [.onStop] and before [.onDetach].
     */
    override fun onDestroy() {
        controlsVisibilityListener = null
        super.onDestroy()
    }

    override fun onSeekStarted(): Boolean {
        playlistManager.invokeSeekStarted()
        return true
    }

    override fun onSeekEnded(seekTime: Long): Boolean {
        playlistManager.invokeSeekEnded(seekTime)
        return true
    }

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

        private const val PLAYLIST_ID = 5L
        override val FRAGMENT_TAG = MediaStreamContent::class.java.simpleName

        override fun newInstance(bundle: Bundle?) =
            MediaStreamContent().apply {
                arguments = bundle
            }
    }
}