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

package co.anitrend.support.crunchyroll.feature.catalog.controller.items

import android.view.View
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.ui.util.StateLayoutConfig
import co.anitrend.support.crunchyroll.feature.catalog.R
import co.anitrend.support.crunchyroll.feature.catalog.databinding.ItemPlaceholderBinding
import com.xwray.groupie.viewbinding.BindableItem

class PlaceHolderItem(
    private val networkState: NetworkState,
    private val onRetry: (() -> Unit)? = null
) : BindableItem<ItemPlaceholderBinding>() {

    override fun getLayout() = R.layout.item_placeholder

    /**
     * Perform any actions required to set up the view for display.
     *
     * @param viewBinding The ViewDataBinding to bind
     * @param position The adapter position
     */
    override fun bind(viewBinding: ItemPlaceholderBinding, position: Int) {
        viewBinding.placeHolderState.stateConfig = StateLayoutConfig(
            loadingMessage = R.string.label_text_loading
        )
        viewBinding.placeHolderState.onWidgetInteraction = View.OnClickListener {
            onRetry?.invoke()
        }
        viewBinding.placeHolderState.setNetworkState(networkState)
    }

    override fun initializeViewBinding(view: View): ItemPlaceholderBinding =
        ItemPlaceholderBinding.bind(view)
}