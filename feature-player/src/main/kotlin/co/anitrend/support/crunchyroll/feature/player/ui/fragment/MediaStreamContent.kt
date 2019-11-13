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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.argument
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.arch.ui.view.widget.SupportStateLayout
import co.anitrend.support.crunchyroll.core.extensions.koinOf
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.domain.stream.models.MediaStreamQuery
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.presenter.StreamPresenter
import co.anitrend.support.crunchyroll.feature.player.viewmodel.MediaStreamViewModel
import com.devbrackets.android.exomedia.ui.widget.VideoView
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaStreamContent : SupportFragment<MediaStream?, CrunchyCorePresenter, List<MediaStream>?>() {

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

    private val payload
            by argument<NavigationTargets.MediaPlayer.Payload>(
                NavigationTargets.MediaPlayer.PAYLOAD
            )

    private lateinit var exoPlayerView: VideoView
    private lateinit var supportStateLayout: SupportStateLayout

    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(this, Observer {
            prepareResults()
        })
        supportViewModel.networkState?.observe(this, Observer {
            supportStateLayout.setNetworkState(it)
        })
        supportViewModel.refreshState?.observe(this, Observer {
            supportStateLayout.setNetworkState(it)
        })
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_media_player, container, false)?.apply {
            exoPlayerView = findViewById(R.id.video_view)
            supportStateLayout = findViewById(R.id.supportStateLayout)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        supportStateLayout.stateConfiguration = koinOf()
        supportStateLayout.onWidgetInteraction = View.OnClickListener {
            supportViewModel.retry()
        }
    }

    override fun onStart() {
        super.onStart()
        setUpViewModelObserver()
    }

    override fun onResume() {
        super.onResume()
        if (!supportViewModel.hasModelData())
            onFetchDataInitialize()
        else
            prepareResults()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [co.anitrend.arch.ui.fragment.contract.ISupportFragmentList]
     * will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        with (exoPlayerView) {
            setOnPreparedListener {
                start()
            }
            setOnErrorListener {
                supportStateLayout.setNetworkState(
                    NetworkState.Error(
                        heading = "Playback Error",
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
        if (!exoPlayerView.isPlaying) {
            val stream = supportPresenter.getOptimalStream(model)
            if (stream != null) {
                supportPresenter.setupMediaSource(
                    stream, exoPlayerView, payload?.episodeThumbnail
                )
                onUpdateUserInterface()
            } else {
                supportStateLayout.setNetworkState(
                    NetworkState.Error(
                        heading = "No Streams Available",
                        message = "Content may be unavailable, please check with original source"
                    )
                )
            }
        }
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
                parameter = MediaStreamQuery(
                    mediaId = it.mediaId
                )
            )
        } ?: supportStateLayout.setNetworkState(
            NetworkState.Error(
                heading = "Invalid Fragment Parameters",
                message = "Invalid or missing payload"
            )
        )
    }

    companion object {
        fun newInstance(bundle: Bundle?): MediaStreamContent {
            return MediaStreamContent().apply {
                arguments = bundle
            }
        }
    }
}