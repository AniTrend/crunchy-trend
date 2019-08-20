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

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.viewmodel.media.CrunchyMediaStreamViewModel
import co.anitrend.support.crunchyroll.data.arch.StreamQualityContract
import co.anitrend.support.crunchyroll.data.model.stream.CrunchyStreamInfo
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaStreamUseCase
import coil.Coil
import coil.api.load
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.google.android.exoplayer2.source.BaseMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy
import com.google.android.exoplayer2.util.Util
import io.wax911.support.core.viewmodel.SupportViewModel
import io.wax911.support.data.model.contract.SupportStateContract
import io.wax911.support.extension.argument
import io.wax911.support.ui.fragment.SupportFragment
import io.wax911.support.ui.view.widget.SupportStateLayout
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

    private val payload by argument<CrunchyMediaStreamUseCase.Payload>(PAYLOAD)

    private lateinit var exoPlayerView: VideoView
    private lateinit var supportStateLayout: SupportStateLayout

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(this, Observer {
            if (it?.stream_data?.streams?.isNullOrEmpty() == false) {

                val mediaDataSourceFactory = DefaultDataSourceFactory(
                    activity,
                    Util.getUserAgent(
                        activity,
                        getString(R.string.app_name)
                    )
                )

                val targetStream = it.stream_data.streams.find { stream ->
                    stream.quality == StreamQualityContract.LOW
                }?.url
                if (targetStream != null) {
                    val mediaUri = Uri.parse(targetStream)

                    val mediaSource = HlsMediaSource.Factory(mediaDataSourceFactory)
                        .setLoadErrorHandlingPolicy(object : LoadErrorHandlingPolicy {
                            override fun getRetryDelayMsFor(
                                dataType: Int,
                                loadDurationMs: Long,
                                exception: IOException?,
                                errorCount: Int
                            ): Long = 5000

                            override fun getMinimumLoadableRetryCount(dataType: Int): Int = 3

                            override fun getBlacklistDurationMsFor(
                                dataType: Int,
                                loadDurationMs: Long,
                                exception: IOException?,
                                errorCount: Int
                            ): Long = 1000
                        })
                        .setAllowChunklessPreparation(true)
                        .createMediaSource(mediaUri)

                    initializePlayer(mediaUri, mediaSource)
                    onUpdateUserInterface()
                } else
                    supportStateLayout.showError(errorMessage = "Target stream is unavaialbe")

            } else {
                supportStateLayout.showError(errorMessage = "No available streams found for this episode")
                Timber.tag(moduleTag).e("Unable to fetch media stream info")
            }
        })
        supportViewModel.networkState?.observe(this, Observer {
            with (supportStateLayout) {
                if (it.status == SupportStateContract.LOADING)
                    showLoading(loadingMessage = R.string.Loading)
                if (it.code != null) {
                    if (it.message == null) {
                        onUpdateUserInterface()
                        showContent()
                    }
                    else
                        showError(errorMessage = it.message)
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
            exoPlayerView = findViewById(R.id.video_view)
            supportStateLayout = findViewById(R.id.supportStateLayout)
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
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [io.wax911.support.ui.fragment.contract.ISupportFragmentList]
     * will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        with (exoPlayerView) {
            setOnPreparedListener {
                start()
            }
        }
    }

    private fun initializePlayer(mediaUri: Uri, mediaSource: BaseMediaSource) {
        with (exoPlayerView) {
            Coil.load(context, payload?.mediaThumbnail) {
                target {
                    runCatching {
                        setPreviewImage(it)
                    }
                }
            }
            setVideoURI(mediaUri, mediaSource)
        }
    }

    /**
     * Handles the complex logic required to dispatch network request to [SupportViewModel]
     * which uses [io.wax911.support.data.repository.SupportRepository] to either request
     * from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportViewModel.model]
     *
     * @see [SupportViewModel.requestBundleLiveData]
     */
    override fun onFetchDataInitialize() {
        payload?.also {
            supportViewModel(
                parameter = it
            )
        } ?: supportStateLayout.showError(errorMessage = "Invalid or missing payload")
    }

    companion object {
        const val PAYLOAD = "FragmentMediaStream:Payload"

        fun newInstance(bundle: Bundle?): FragmentMediaStream {
            return FragmentMediaStream().apply {
                arguments = bundle
            }
        }
    }
}