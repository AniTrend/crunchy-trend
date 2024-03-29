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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.domain.entities.LoadState
import co.anitrend.arch.domain.entities.RequestError
import co.anitrend.arch.extension.ext.*
import co.anitrend.support.crunchyroll.android.binding.IBindingView
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.core.extensions.createDialog
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.model.UserAgent
import co.anitrend.support.crunchyroll.core.ui.fragment.CrunchyFragment
import co.anitrend.support.crunchyroll.core.ui.get
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.domain.stream.models.CrunchyMediaStreamQuery
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.component.SourceFactoryProvider
import co.anitrend.support.crunchyroll.feature.player.databinding.FragmentMediaPlayerBinding
import co.anitrend.support.crunchyroll.feature.player.model.track.contract.IMediaTrack
import co.anitrend.support.crunchyroll.feature.player.plugin.MediaPluginImpl
import co.anitrend.support.crunchyroll.feature.player.plugin.PlaylistManagerPluginImpl
import co.anitrend.support.crunchyroll.feature.player.presenter.StreamPresenter
import co.anitrend.support.crunchyroll.feature.player.viewmodel.MediaStreamViewModel
import co.anitrend.support.crunchyroll.navigation.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.devbrackets.android.exomedia.listener.VideoControlsSeekListener
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener
import com.devbrackets.android.exomedia.ui.widget.VideoControls
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaStreamContent(
    override var inflateLayout: Int = R.layout.fragment_media_player,
    private val sourceFactoryProvider: SourceFactoryProvider,
    private val playlistManager: PlaylistManagerPluginImpl,
    private val presenter: StreamPresenter,
    userAgent: UserAgent
) : CrunchyFragment(), IBindingView<FragmentMediaPlayerBinding> {

    override var binding: FragmentMediaPlayerBinding? = null

    private val qualityButton by lazy(UNSAFE) {
        AppCompatImageButton(requireContext()).apply {
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
    private val audioButton by lazy(UNSAFE) {
        AppCompatImageButton(requireContext()).apply {
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
    private val captionButton by lazy(UNSAFE) {
        AppCompatImageButton(requireContext()).apply {
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

    private val mediaPlugin by lazy(UNSAFE) {
        MediaPluginImpl(
            lifecycleScope,
            requireBinding().exoMediaVideoView,
            userAgent.identifier,
            DefaultBandwidthMeter.Builder(requireContext()).build(),
            sourceFactoryProvider
        )
    }

    private val payload by argument<MediaPlayer.Payload>(MediaPlayer.extraKey)

    private var controlsVisibilityListener: VideoControlsVisibilityListener? = null

    private val viewModel by viewModel<MediaStreamViewModel>()

    private fun showDialogUsing(message: Int, mediaTracks: List<IMediaTrack>?) {
        if (mediaTracks.isNullOrEmpty()) {
            Snackbar.make(requireBinding().root, message, Snackbar.LENGTH_SHORT).show()
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
        viewModelState().model.observe(viewLifecycleOwner) {
            lifecycleScope.launch { prepareResults(it) }
        }
        viewModelState().loadState.observe(viewLifecycleOwner) {
            requireBinding().supportStateLayout.loadStateFlow.value = it
        }
        viewModelState().refreshState.observe(viewLifecycleOwner) {
            requireBinding().supportStateLayout.loadStateFlow.value = it
        }
    }

    /**
     * Additional initialization to be done in this method, this method will be called in
     * [androidx.fragment.app.FragmentActivity.onCreate].
     *
     * @param savedInstanceState
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            if (!viewModelState().isEmpty())
                onFetchDataInitialize()
        }
        lifecycleScope.launchWhenResumed {
            requireBinding().supportStateLayout.interactionFlow
                .filterNotNull()
                .debounce(DEBOUNCE_DURATION)
                .collect {
                    viewModelState().retry()
                }
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional, and
     * non-graphical fragments can return null. This will be called between
     * [onCreate] & [onActivityCreated].
     *
     * A default View can be returned by calling [Fragment] in your
     * constructor. Otherwise, this method returns null.
     *
     * It is recommended to __only__ inflate the layout in this method and move
     * logic that operates on the returned View to [onViewCreated].
     *
     * If you return a View from here, you will later be called in [onDestroyView]
     * when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be
     * attached to. The fragment should not add the view itself, but this can be used to generate
     * the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding = view?.let(FragmentMediaPlayerBinding::bind)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch { onUpdateUserInterface() }
    }

    private fun onUpdateUserInterface() {
        requireBinding().supportStateLayout.stateConfigFlow.value = get()
        mediaPlugin.onInitializing()
        mediaPlugin.setVisibilityListener(
            controlsVisibilityListener
        )
        (requireBinding().exoMediaVideoView.videoControls as? VideoControls)?.also {
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
            if (requireBinding().exoMediaVideoView.trackSelectionAvailable()) {
                it.addExtraView(qualityButton)
                it.addExtraView(audioButton)
                it.addExtraView(captionButton)
            }
        }
    }

    private fun prepareResults(stream: MediaStream) {
        val mediaStreamItem = presenter.toStreamItem(stream, payload)

        playlistManager.id = mediaStreamItem.id
        playlistManager.setParameters(listOf(mediaStreamItem), 0)
        playlistManager.play(mediaStreamItem.mediaPlayHead.toLong(),false)
    }

    private fun onFetchDataInitialize() {
        val mediaStreamPayload = payload
        if (mediaStreamPayload != null) {
            viewModel.state(
                parameter = CrunchyMediaStreamQuery(
                    mediaId = mediaStreamPayload.mediaId
                )
            )
        } else {
            requireBinding().supportStateLayout.loadStateFlow.value =
                LoadState.Error(
                    RequestError(
                        topic = "Invalid fragment parameters ${Emote.Cry}",
                        description = "Invalid or missing payload, request cannot be processed"
                    )
                )
        }
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
                requireBinding().exoMediaVideoView.showControls()
            }
        }
    }
}