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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentOnAttachListener
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.extension.ext.getCompatColor
import co.anitrend.support.crunchyroll.core.ui.model.FragmentItem
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.ui.commit
import co.anitrend.support.crunchyroll.feature.player.R
import co.anitrend.support.crunchyroll.feature.player.databinding.ActivityStreamingBinding
import co.anitrend.support.crunchyroll.feature.player.ui.fragment.MediaStreamContent
import com.devbrackets.android.exomedia.listener.VideoControlsVisibilityListener
import kotlinx.coroutines.launch

class MediaPlayerScreen : CrunchyActivity<ActivityStreamingBinding>(), VideoControlsVisibilityListener,
    FragmentOnAttachListener {

    internal var fullScreenListener: MediaStreamContent.FullScreenListener? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreamingBinding.inflate(layoutInflater)
        setContentView(requireBinding().root)
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {
        lifecycleScope.launch { onUpdateUserInterface() }
    }

    private fun onUpdateUserInterface() {

        supportFragmentManager.addFragmentOnAttachListener(this)

        currentFragmentTag = FragmentItem(
            parameter = intent.extras,
            fragment = MediaStreamContent::class.java
        ).commit(R.id.contentFrame, this) {}

        initUiFlags()
    }

    public override fun onDestroy() {
        supportFragmentManager.removeFragmentOnAttachListener(this)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        super.onDestroy()
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

    /**
     * Called after the fragment has been attached to its host. This is called
     * immediately after [Fragment.onAttach] and before
     * [Fragment.onAttach] has been called on any child fragments.
     *
     * @param fragmentManager FragmentManager the fragment is now attached to. This will
     * be the same FragmentManager that is returned by
     * [Fragment.getParentFragmentManager].
     * @param fragment Fragment that just received a callback to [Fragment.onAttach]
     */
    override fun onAttachFragment(fragmentManager: FragmentManager, fragment: Fragment) {
        if (fragment is MediaStreamContent)
            fullScreenListener = fragment.FullScreenListener()
    }
}