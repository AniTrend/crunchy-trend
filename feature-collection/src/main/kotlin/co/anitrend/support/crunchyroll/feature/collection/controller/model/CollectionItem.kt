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

package co.anitrend.support.crunchyroll.feature.collection.controller.model

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import co.anitrend.arch.recycler.action.contract.ISupportSelectionMode
import co.anitrend.arch.recycler.common.ClickableItem
import co.anitrend.arch.recycler.holder.SupportViewHolder
import co.anitrend.support.crunchyroll.core.android.recycler.model.RecyclerItemBinding
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection
import co.anitrend.support.crunchyroll.feature.collection.R
import co.anitrend.support.crunchyroll.feature.collection.databinding.AdapterCollectionBinding
import co.anitrend.support.crunchyroll.feature.collection.presenter.CollectionPresenter.Companion.titleWithSeason
import kotlinx.coroutines.flow.MutableStateFlow

data class CollectionItem(
    val entity: CrunchyCollection?
) : RecyclerItemBinding<AdapterCollectionBinding>(entity?.collectionId ?: 0) {

    /**
     * Called when the [view] needs to be setup, this could be to set click listeners,
     * assign text, load images, e.t.c
     *
     * @param view view that was inflated
     * @param position current position
     * @param payloads optional payloads which maybe empty
     * @param stateFlow observable to broadcast click events
     */
    override fun bind(
        view: View,
        position: Int,
        payloads: List<Any>,
        stateFlow: MutableStateFlow<ClickableItem>,
        selectionMode: ISupportSelectionMode<Long>?
    ) {
        binding = AdapterCollectionBinding.bind(view)
        requireBinding().collectionTitle.text = entity?.titleWithSeason()
        requireBinding().collectionDescription.text = entity?.description
        requireBinding().container.setOnClickListener {
            stateFlow.value =
                ClickableItem.Data(
                    data = entity,
                    view = view
                )
        }
    }

    /**
     * Called when the view needs to be recycled for reuse, clear any held references
     * to objects, stop any asynchronous work, e.t.c
     */
    override fun unbind(view: View) {
        binding?.container?.setOnClickListener(null)
        super.unbind(view)
    }

    /**
     * Provides a preferred span size for the item
     *
     * @param spanCount current span count which may also be [INVALID_SPAN_COUNT]
     * @param position position of the current item
     * @param resources optionally useful for dynamic size check with different configurations
     */
    override fun getSpanSize(spanCount: Int, position: Int, resources: Resources) =
        resources.getInteger(R.integer.single_list_size)

    companion object {
        internal fun createViewHolder(
            viewGroup: ViewGroup,
            layoutInflater: LayoutInflater
        ) = AdapterCollectionBinding.inflate(
            layoutInflater, viewGroup, false
        ).let { SupportViewHolder(it) }

        internal val DIFFER =
            object : DiffUtil.ItemCallback<CrunchyCollection>() {
                override fun areItemsTheSame(
                    oldItem: CrunchyCollection,
                    newItem: CrunchyCollection
                ): Boolean {
                    return oldItem.collectionId == newItem.collectionId
                }

                override fun areContentsTheSame(
                    oldItem: CrunchyCollection,
                    newItem: CrunchyCollection
                ): Boolean {
                    return oldItem.hashCode() == newItem.hashCode()
                }
            }
    }
}