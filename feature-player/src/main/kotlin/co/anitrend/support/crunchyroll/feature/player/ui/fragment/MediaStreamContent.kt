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
import android.webkit.WebView
import androidx.lifecycle.Observer
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.argument
import co.anitrend.arch.extension.attachComponent
import co.anitrend.arch.extension.detachComponent
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.model.Emote
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.domain.stream.models.CrunchyMediaStreamQuery
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.component.SourceFactoryProvider
import co.anitrend.support.crunchyroll.feature.player.presenter.StreamPresenter
import co.anitrend.support.crunchyroll.feature.player.viewmodel.MediaStreamViewModel
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import kotlinx.android.synthetic.main.fragment_media_player.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MediaStreamContent : SupportFragment<MediaStream?, CrunchyCorePresenter, List<MediaStream>?>() {

    private val payload
            by argument<NavigationTargets.MediaPlayer.Payload>(
                NavigationTargets.MediaPlayer.PAYLOAD
            )

    private val sourceFactoryProvider by inject<SourceFactoryProvider>()

    private var controlsVisibilityListener: VideoControlsVisibilityListener? = null

    override val inflateLayout = R.layout.fragment_media_player

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

    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(viewLifecycleOwner, Observer {
            prepareResults()
        })
        supportViewModel.networkState?.observe(viewLifecycleOwner, Observer {
            supportStateLayout.setNetworkState(it)
        })
        supportViewModel.refreshState?.observe(viewLifecycleOwner, Observer {
            supportStateLayout.setNetworkState(it)
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
                else
                    prepareResults()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportStateLayout.stateConfiguration = get()
        supportStateLayout.onWidgetInteraction = View.OnClickListener {
            supportViewModel.retry()
        }
    }

    override fun onPause() {
        exoMediaVideoView.pause()
        super.onPause()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [co.anitrend.arch.ui.fragment.contract.ISupportFragmentList]
     * will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        with (exoMediaVideoView) {
            supportPresenter.customizeControls(
                {

                },
                this,
                videoControlsCore,
                payload,
                controlsVisibilityListener!!
            )
            setOnPreparedListener {
                start()
            }
            setOnErrorListener {
                supportStateLayout.setNetworkState(
                    NetworkState.Error(
                        heading = "Playback Error ${Emote.Shocked}",
                        message = it.message
                    )
                )
                it.printStackTrace()
                false
            }
        }
    }

    private fun prepareResults() {
        val model = supportViewModel.model.value
        if (exoMediaVideoView.videoUri == null) {
            val stream = supportPresenter.getOptimalStream(model)
            if (stream != null) {
                supportPresenter.setupMediaSource(
                    sourceFactoryProvider,
                    stream,
                    exoMediaVideoView,
                    payload?.episodeThumbnail,
                    viewLifecycleOwner
                )
                onUpdateUserInterface()
            } else {
                supportStateLayout.setNetworkState(
                    NetworkState.Error(
                        heading = "No Streams Available ${Emote.Eyes}",
                        message = "Content may be unavailable, please check with original source"
                    )
                )
            }
        } else
            if (!exoMediaVideoView.isPlaying)
                exoMediaVideoView.start()
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
                heading = "Invalid Fragment Parameters ${Emote.Cry}",
                message = "Invalid or missing payload, request will not be processed"
            )
        )
    }

    /**
     * Called when a fragment is first attached to its context.
     * [.onCreate] will be called after this.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is VideoControlsVisibilityListener)
            controlsVisibilityListener = context
        attachComponent(sourceFactoryProvider)
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after [.onDestroy].
     */
    override fun onDetach() {
        detachComponent(sourceFactoryProvider)
        super.onDetach()
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after [.onStop] and before [.onDetach].
     */
    override fun onDestroy() {
        exoMediaVideoView?.release()
        controlsVisibilityListener = null
        super.onDestroy()
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

    companion object {
        fun newInstance(bundle: Bundle?): MediaStreamContent {
            return MediaStreamContent().apply {
                arguments = bundle
            }
        }
    }
}