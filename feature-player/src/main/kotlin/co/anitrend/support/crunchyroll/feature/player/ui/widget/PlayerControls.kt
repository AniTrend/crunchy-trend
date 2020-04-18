/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.feature.player.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.devbrackets.android.exomedia.ui.widget.VideoControls

class PlayerControls : VideoControls {

    constructor(context: Context) :
            super(context)
    constructor(context: Context, attrs: AttributeSet) :
            super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    /**
     * Used to retrieve the layout resource identifier to inflate
     *
     * @return The layout resource identifier to inflate
     */
    override val layoutResource: Int
        get() = TODO("Not yet implemented")

    /**
     * Performs any initialization steps such as retrieving views, registering listeners,
     * and updating any drawables.
     *
     * @param context The context to use for retrieving the correct layout
     */
    override fun setup(context: Context) {
        super.setup(context)
    }

    /**
     * Performs the control visibility animation for showing or hiding
     * this view
     *
     * @param toVisible True if the view should be visible at the end of the animation
     */
    override fun animateVisibility(toVisible: Boolean) {
        TODO("Not yet implemented")
    }

    /**
     * Update the controls to indicate that the video is no longer loading
     * which will re-display the play/pause, progress, etc. controls
     */
    override fun finishLoading() {
        TODO("Not yet implemented")
    }

    /**
     * Sets the video duration in Milliseconds to display
     * at the end of the progress bar
     *
     * @param duration The duration of the video in milliseconds
     */
    override fun setDuration(duration: Long) {
        TODO("Not yet implemented")
    }

    /**
     * Sets the current video position, updating the seek bar
     * and the current time field
     *
     * @param position The position in milliseconds
     */
    override fun setPosition(position: Long) {
        TODO("Not yet implemented")
    }

    /**
     * Update the controls to indicate that the video
     * is loading.
     *
     * @param initialLoad `true` if the loading is the initial state, not for seeking or buffering
     */
    override fun showLoading(initialLoad: Boolean) {
        TODO("Not yet implemented")
    }

    /**
     * Performs the progress update on the current time field,
     * and the seek bar
     *
     * @param position The position in milliseconds
     * @param duration The duration of the video in milliseconds
     * @param bufferPercent The integer percent that is buffered [0, 100] inclusive
     */
    override fun updateProgress(position: Long, duration: Long, bufferPercent: Int) {
        TODO("Not yet implemented")
    }

    /**
     * Update the current visibility of the text block independent of
     * the controls visibility
     */
    override fun updateTextContainerVisibility() {
        TODO("Not yet implemented")
    }
}