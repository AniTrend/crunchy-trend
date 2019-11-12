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

package co.anitrend.support.crunchyroll.core.android.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ScrollAwareFABBehavior(context: Context?, attrs: AttributeSet?) :
    FloatingActionButton.Behavior(context, attrs) {

    /**
     * Called when a descendant of the CoordinatorLayout attempts to initiate a nested scroll.
     *
     * Any Behavior associated with any direct child of the CoordinatorLayout may respond
     * to this event and return true to indicate that the CoordinatorLayout should act as
     * a nested scrolling parent for this scroll. Only Behaviors that return true from
     * this method will receive subsequent nested scroll events.
     *
     * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
     * associated with
     * @param child the child view of the CoordinatorLayout this Behavior is associated with
     * @param directTargetChild the child view of the CoordinatorLayout that either is or
     * contains the target of the nested scroll operation
     * @param target the descendant view of the CoordinatorLayout initiating the nested scroll
     * @param axes the axes that this nested scroll applies to. See
     * [ViewCompat.SCROLL_AXIS_HORIZONTAL],
     * [ViewCompat.SCROLL_AXIS_VERTICAL]
     * @param type the type of input which cause this scroll event
     * @return true if the Behavior wishes to accept this nested scroll
     *
     * @see NestedScrollingParent2.onStartNestedScroll
     */
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    /**
     * Called when a nested scroll in progress has updated and the target has scrolled or
     * attempted to scroll.
     *
     * Any Behavior associated with the direct child of the CoordinatorLayout may elect
     * to accept the nested scroll as part of [.onStartNestedScroll]. Each Behavior
     * that returned true will receive subsequent nested scroll events for that nested scroll.
     *
     * `onNestedScroll` is called each time the nested scroll is updated by the
     * nested scrolling child, with both consumed and unconsumed components of the scroll
     * supplied in pixels. *Each Behavior responding to the nested scroll will receive the
     * same values.*
     *
     * @param coordinatorLayout the CoordinatorLayout parent of the view this Behavior is
     * associated with
     * @param child the child view of the CoordinatorLayout this Behavior is associated with
     * @param target the descendant view of the CoordinatorLayout performing the nested scroll
     * @param dxConsumed horizontal pixels consumed by the target's own scrolling operation
     * @param dyConsumed vertical pixels consumed by the target's own scrolling operation
     * @param dxUnconsumed horizontal pixels not consumed by the target's own scrolling
     * operation, but requested by the user
     * @param dyUnconsumed vertical pixels not consumed by the target's own scrolling operation,
     * but requested by the user
     * @param type the type of input which cause this scroll event
     * @param consumed output. Upon this method returning, should contain the scroll
     * distances consumed by this Behavior
     *
     * @see NestedScrollingParent3.onNestedScroll
     */
    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
        if (dyConsumed > 0 && child.visibility == View.VISIBLE)
            child.hide()
        else if (dyConsumed < 0 && child.visibility != View.VISIBLE)
            child.show()
    }
}