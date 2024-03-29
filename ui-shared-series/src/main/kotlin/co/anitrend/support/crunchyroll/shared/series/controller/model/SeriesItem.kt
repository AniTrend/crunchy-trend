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

package co.anitrend.support.crunchyroll.shared.series.controller.model

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import co.anitrend.arch.recycler.action.contract.ISupportSelectionMode
import co.anitrend.arch.recycler.common.ClickableItem
import co.anitrend.arch.recycler.holder.SupportViewHolder
import co.anitrend.support.crunchyroll.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.android.recycler.model.RecyclerItemBinding
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.shared.series.R
import co.anitrend.support.crunchyroll.shared.series.databinding.AdapterDiscoverSeriesBinding
import coil.request.Disposable
import kotlinx.coroutines.flow.MutableStateFlow

data class SeriesItem(
    val entity: CrunchySeries?
) : RecyclerItemBinding<AdapterDiscoverSeriesBinding>(entity?.seriesId ?: 0) {

    private var disposable: Disposable? = null

    /**
     * Called when the [view] needs to be setup, this could be to set click listeners,
     * assign text, load images, e.t.c
     *
     * @param view view that was inflated
     * @param position current position
     * @param payloads optional payloads which maybe empty
     * @param stateFlow observable to broadcast click events
     * @param selectionMode action mode helper or null if none was provided
     */
    override fun bind(
        view: View,
        position: Int,
        payloads: List<Any>,
        stateFlow: MutableStateFlow<ClickableItem>,
        selectionMode: ISupportSelectionMode<Long>?
    ) {
        binding = AdapterDiscoverSeriesBinding.bind(view)
        disposable = requireBinding().seriesImage.setImageUrl(entity?.portraitImage)
        requireBinding().seriesName.text = entity?.name
        requireBinding().seriesDescription.text = entity?.description
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
        disposable?.dispose()
        disposable = null
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
        ) = AdapterDiscoverSeriesBinding.inflate(
            layoutInflater, viewGroup, false
        ).let { SupportViewHolder(it) }

        internal val DIFFER =
            object : DiffUtil.ItemCallback<CrunchySeries>() {
                override fun areItemsTheSame(
                    oldItem: CrunchySeries,
                    newItem: CrunchySeries
                ): Boolean {
                    return oldItem.seriesId == newItem.seriesId
                }

                override fun areContentsTheSame(
                    oldItem: CrunchySeries,
                    newItem: CrunchySeries
                ): Boolean {
                    return oldItem.rating == newItem.rating &&
                            oldItem.mediaCount == newItem.mediaCount
                }

                override fun getChangePayload(
                    oldItem: CrunchySeries,
                    newItem: CrunchySeries
                ): Any? {
                    return super.getChangePayload(oldItem, newItem)
                }
            }
    }
}