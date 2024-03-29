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

package co.anitrend.support.crunchyroll.feature.listing.controller.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import co.anitrend.arch.recycler.action.contract.ISupportSelectionMode
import co.anitrend.arch.recycler.common.ClickableItem
import co.anitrend.arch.recycler.holder.SupportViewHolder
import co.anitrend.support.crunchyroll.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.android.recycler.model.RecyclerItemBinding
import co.anitrend.support.crunchyroll.domain.episode.entities.CrunchyEpisodeFeed
import co.anitrend.support.crunchyroll.feature.listing.databinding.AdapterMediaFeedBinding
import coil.request.Disposable
import kotlinx.coroutines.flow.MutableStateFlow

data class EpisodeFeedItem(
    val entity: CrunchyEpisodeFeed?
) : RecyclerItemBinding<AdapterMediaFeedBinding>(entity?.id ?: 0) {

    private var disposable: Disposable? = null

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
        binding = AdapterMediaFeedBinding.bind(view)
        disposable = requireBinding().mediaThumbnail.setImageUrl(entity)
        requireBinding().mediaTitle.text = entity?.title
        requireBinding().mediaDuration.text = entity?.episodeDuration
        requireBinding().mediaThumbnail.setOnClickListener {
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
        binding?.mediaThumbnail?.setOnClickListener(null)
        disposable?.dispose()
        disposable = null
        super.unbind(view)
    }

    companion object {
        internal fun createViewHolder(
            viewGroup: ViewGroup,
            layoutInflater: LayoutInflater
        ) = AdapterMediaFeedBinding.inflate(
        layoutInflater, viewGroup, false
        ).let { SupportViewHolder(it) }

        internal val DIFFER =
            object : DiffUtil.ItemCallback<CrunchyEpisodeFeed>() {
                override fun areItemsTheSame(
                    oldItem: CrunchyEpisodeFeed,
                    newItem: CrunchyEpisodeFeed
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: CrunchyEpisodeFeed,
                    newItem: CrunchyEpisodeFeed
                ): Boolean {
                    return oldItem.id == newItem.id &&
                            oldItem.title == newItem.title &&
                            oldItem.description == newItem.description &&
                            oldItem.isCountryWhiteListed == newItem.isCountryWhiteListed
                }

                override fun getChangePayload(
                    oldItem: CrunchyEpisodeFeed,
                    newItem: CrunchyEpisodeFeed
                ): Any? {
                    return super.getChangePayload(oldItem, newItem)
                }
            }
    }
}