/*
 *    Copyright 2021 AniTrend
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

package co.anitrend.support.crunchyroll.core.android.recycler.model

import android.content.res.Resources
import android.view.View
import androidx.viewbinding.ViewBinding
import co.anitrend.arch.recycler.action.decorator.ISelectionDecorator
import co.anitrend.arch.recycler.model.contract.IRecyclerItem
import co.anitrend.support.crunchyroll.core.android.R
import co.anitrend.support.crunchyroll.core.android.binding.IBindingView


abstract class RecyclerItemBinding<B : ViewBinding>(
    override val id: Long
) : IRecyclerItem, IBindingView<B> {

    override var binding: B? = null

    /**
     * Decorator that can be used to style this item when it is selected or unselected
     */
    override val decorator: ISelectionDecorator =
        object : ISelectionDecorator {
            // uses the default implementation of decorator
        }

    /**
     * If selection mode can be used, this will allow automatic styling of elements based
     * on selection state when the view item/s are drawn
     */
    override val supportsSelectionMode: Boolean = false

    /**
     * Called when the view needs to be recycled for reuse, clear any held references
     * to objects, stop any asynchronous work, e.t.c
     */
    override fun unbind(view: View) {
        binding = null
    }

    /**
     * Provides a preferred span size for the item, defaulted to [R.integer.single_list_size]
     *
     * @param spanCount current span count which may also be [INVALID_SPAN_COUNT]
     * @param position position of the current item
     * @param resources optionally useful for dynamic size check with different configurations
     */
    override fun getSpanSize(
        spanCount: Int,
        position: Int,
        resources: Resources
    )= resources.getInteger(R.integer.single_list_size)
}