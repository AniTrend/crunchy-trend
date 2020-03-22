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

package co.anitrend.support.crunchyroll.feature.player.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.commit
import co.anitrend.arch.extension.getCompatColor
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.player.ui.fragment.MediaStreamContent
import com.devbrackets.android.exomedia.ExoMedia
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class MediaPlayerScreen : CrunchyActivity<Nothing, CrunchyCorePresenter>(),
    VideoControlsVisibilityListener {

    private var fullScreenListener: MediaStreamContent.FullScreenListener? = null

    /**
     * Determines the appropriate fullscreen flags based on the
     * systems API version.
     *
     * @return The appropriate decor view flags to enter fullscreen mode when supported
     */
    private val fullscreenUiFlags: Int
        get() = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    private val stableUiFlags: Int
        get() = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {
        super.configureActivity()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            with (window) {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = getCompatColor(R.color.colorScrim)
                navigationBarColor = getCompatColor(R.color.colorScrim)
            }
        }
    }

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<CrunchyCorePresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming)
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
        injectFeatureModules()
        val cacheFactory = get<ExoMedia.DataSourceFactoryProvider>()
        ExoMedia.setDataSourceFactoryProvider(cacheFactory)
        onUpdateUserInterface()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [co.anitrend.arch.ui.view.contract.ISupportFragmentActivity]
     * will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        if (supportFragmentActivity == null) {
            supportFragmentActivity = MediaStreamContent.newInstance(intent.extras).apply {
                fullScreenListener = FullScreenListener()
                supportFragmentManager.commit {
                    //setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    replace(R.id.contentFrame, this@apply, tag)
                }
            }
        }
        initUiFlags()
    }

    public override fun onDestroy() {
        super.onDestroy()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

    private fun goFullscreen() {
        setUiFlags(true)
    }

    private fun exitFullscreen() {
        setUiFlags(false)
    }

    /**
     * Correctly sets up the fullscreen flags to avoid popping when we switch
     * between fullscreen and not
     */
    private fun initUiFlags() {
        window.decorView.systemUiVisibility = stableUiFlags
        window.decorView.setOnSystemUiVisibilityChangeListener(fullScreenListener)
    }

    /**
     * Applies the correct flags to the windows decor view to enter
     * or exit fullscreen mode
     *
     * @param fullscreen True if entering fullscreen mode
     */
    private fun setUiFlags(fullscreen: Boolean) {
        window.decorView.systemUiVisibility = if (fullscreen) fullscreenUiFlags else stableUiFlags
    }

    override fun onControlsShown() {
        if (fullScreenListener?.lastVisibility != View.SYSTEM_UI_FLAG_VISIBLE)
            exitFullscreen()
    }

    override fun onControlsHidden() {
        goFullscreen()
    }
}