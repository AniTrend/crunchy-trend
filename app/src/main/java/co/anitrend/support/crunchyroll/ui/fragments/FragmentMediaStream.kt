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

package co.anitrend.support.crunchyroll.ui.fragments

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.viewmodel.media.CrunchyMediaStreamViewModel
import co.anitrend.support.crunchyroll.data.arch.StreamQuality
import co.anitrend.support.crunchyroll.data.arch.StreamQualityContract
import co.anitrend.support.crunchyroll.data.model.stream.CrunchyStreamInfo
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaStreamUseCase
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.BaseMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import io.wax911.support.core.viewmodel.SupportViewModel
import io.wax911.support.extension.gone
import io.wax911.support.extension.visible
import io.wax911.support.ui.fragment.SupportFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.IOException

class FragmentMediaStream : SupportFragment<CrunchyStreamInfo?, CrunchyCorePresenter, CrunchyStreamInfo?>() {

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<CrunchyCorePresenter>()

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return view model of the given type
     */
    override val supportViewModel by viewModel<CrunchyMediaStreamViewModel>()

    private var progressBar: ProgressBar? = null
    private var exoPlayerView: PlayerView? = null

    private var player: SimpleExoPlayer? = null
    private val mediaDataSourceFactory: DataSource.Factory by lazy(LazyThreadSafetyMode.NONE) {
        DefaultDataSourceFactory(activity, Util.getUserAgent(activity, getString(R.string.app_name)))
    }

    private val videoTrackSelectionFactory by lazy(LazyThreadSafetyMode.NONE) {
        val bandwidthMeter = DefaultBandwidthMeter.Builder(context)
            .experimental_resetOnNetworkTypeChange(true)
            .build()

        AdaptiveTrackSelection.Factory()
    }

    private var lastSeenTrackGroupArray: TrackGroupArray? = null
    private var currentWindow: Int = 0
    private var playbackPosition: Long = 0

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(this, Observer {
            if (it != null) {
                val streams = it.stream_data.streams
                val mediaSource = HlsMediaSource.Factory(mediaDataSourceFactory)
                    .setLoadErrorHandlingPolicy(object: LoadErrorHandlingPolicy {

                        /**
                         * Returns the number of milliseconds to wait before attempting the load again, or [ ][C.TIME_UNSET] if the error is fatal and should not be retried.
                         *
                         *
                         * [Loader] clients may ignore the retry delay returned by this method in order to wait
                         * for a specific event before retrying. However, the load is retried if and only if this method
                         * does not return [C.TIME_UNSET].
                         *
                         * @param dataType One of the [C.DATA_TYPE_*][C] constants indicating the type of data to
                         * load.
                         * @param loadDurationMs The duration in milliseconds of the load up to the point at which the
                         * error occurred, including any previous attempts.
                         * @param exception The load error.
                         * @param errorCount The number of errors this load has encountered, including this one.
                         * @return The number of milliseconds to wait before attempting the load again, or [     ][C.TIME_UNSET] if the error is fatal and should not be retried.
                         */
                        override fun getRetryDelayMsFor(
                            dataType: Int,
                            loadDurationMs: Long,
                            exception: IOException?,
                            errorCount: Int
                        ): Long = 5000

                        /**
                         * Returns the minimum number of times to retry a load in the case of a load error, before
                         * propagating the error.
                         *
                         * @param dataType One of the [C.DATA_TYPE_*][C] constants indicating the type of data to
                         * load.
                         * @return The minimum number of times to retry a load in the case of a load error, before
                         * propagating the error.
                         * @see Loader.startLoading
                         */
                        override fun getMinimumLoadableRetryCount(dataType: Int): Int = 3

                        /**
                         * Returns the number of milliseconds for which a resource associated to a provided load error
                         * should be blacklisted, or [C.TIME_UNSET] if the resource should not be blacklisted.
                         *
                         * @param dataType One of the [C.DATA_TYPE_*][C] constants indicating the type of data to
                         * load.
                         * @param loadDurationMs The duration in milliseconds of the load up to the point at which the
                         * error occurred, including any previous attempts.
                         * @param exception The load error.
                         * @param errorCount The number of errors this load has encountered, including this one.
                         * @return The blacklist duration in milliseconds, or [C.TIME_UNSET] if the resource should
                         * not be blacklisted.
                         */
                        override fun getBlacklistDurationMsFor(
                            dataType: Int,
                            loadDurationMs: Long,
                            exception: IOException?,
                            errorCount: Int
                        ): Long = 1000
                    })
                    .setAllowChunklessPreparation(true)
                    .createMediaSource(
                        Uri.parse(
                            streams.find { stream ->
                                stream.quality == StreamQualityContract.LOW
                            }?.url
                        )
                    )

                initializePlayer(mediaSource)
                onUpdateUserInterface()
            } else
                Timber.tag(moduleTag).e("Unable to fetch media stream info")
        })
        supportViewModel.networkState?.observe(this, Observer {
            if (it.code != null) {
                when {
                    it.message == null -> onUpdateUserInterface()
                    else -> {
                        progressBar?.gone()
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * [.onCreate] and [.onActivityCreated].
     *
     * A default View can be returned by calling [.Fragment] in your
     * constructor. Otherwise, this method returns null.
     *
     *
     * It is recommended to **only** inflate the layout in this method and move
     * logic that operates on the returned View to [.onViewCreated].
     *
     *
     * If you return a View from here, you will later be called in
     * [.onDestroyView] when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_media_player, container, false)?.apply {
            progressBar = findViewById(R.id.progressBar)
            exoPlayerView = findViewById(R.id.playerView)
        }
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to [SupportFragment.onStart] of the containing
     * Activity's lifecycle.
     */
    override fun onStart() {
        super.onStart()
        setUpViewModelObserver()
        onFetchDataInitialize()
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to [Activity.onStop] of the containing
     * Activity's lifecycle.
     */
    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23)
            releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23)
            releasePlayer()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        exoPlayerView?.setShutterBackgroundColor(Color.TRANSPARENT)
        exoPlayerView?.player = player
        exoPlayerView?.requestFocus()

        lastSeenTrackGroupArray = null
        progressBar?.gone()
    }

    private fun initializePlayer(mediaSource: BaseMediaSource) {
        val loadControl = DefaultLoadControl()
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(activity, trackSelector, loadControl).also {
            it.addListener(playerListener)
            it.prepare(mediaSource, false, false)
            it.playWhenReady = true
        }
    }

    /**
     * Handles the complex logic required to dispatch network request to [SupportViewModel]
     * which uses [SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportViewModel.model]
     *
     * @see [SupportViewModel.requestBundleLiveData]
     */
    override fun onFetchDataInitialize() {
        progressBar?.visible()
        val payload = arguments?.getParcelable<CrunchyMediaStreamUseCase.Payload>(PAYLOAD)
        if (payload != null)
            supportViewModel(
                parameter = payload
            )
        else
            Toast.makeText(context, "Invalid parameter/s", Toast.LENGTH_SHORT).show()
    }

    private fun updateStartPosition() {
        player?.also {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.playWhenReady = true
        }
    }

    private fun releasePlayer() {
        updateStartPosition()
        player?.removeListener(playerListener)
        player?.release()
    }

    private val playerListener = object: Player.EventListener {
        /**
         * Called when the player starts or stops loading the source.
         *
         * @param isLoading Whether the source is currently being loaded.
         */
        override fun onLoadingChanged(isLoading: Boolean) {
            super.onLoadingChanged(isLoading)
            if (!isLoading)
                progressBar?.visible()
            else
                progressBar?.gone()
        }
    }

    companion object {
        private const val PAYLOAD = "FragmentMediaStream:Payload"

        fun newInstance(payload: CrunchyMediaStreamUseCase.Payload): FragmentMediaStream {
            return FragmentMediaStream().apply {
                arguments = Bundle().apply {
                    putParcelable(PAYLOAD, payload)
                }
            }
        }
    }
}