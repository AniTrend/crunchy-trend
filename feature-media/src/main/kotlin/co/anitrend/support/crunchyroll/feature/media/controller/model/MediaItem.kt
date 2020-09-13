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

package co.anitrend.support.crunchyroll.feature.media.controller.model

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import co.anitrend.arch.recycler.action.contract.ISupportSelectionMode
import co.anitrend.arch.recycler.common.ClickableItem
import co.anitrend.arch.recycler.common.DefaultClickableItem
import co.anitrend.arch.recycler.holder.SupportViewHolder
import co.anitrend.arch.recycler.model.RecyclerItem
import co.anitrend.support.crunchyroll.core.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.domain.media.entities.CrunchyMedia
import co.anitrend.support.crunchyroll.feature.media.R
import co.anitrend.support.crunchyroll.feature.media.databinding.AdapterMediaBinding
import co.anitrend.support.crunchyroll.feature.media.presenter.MediaPresenter.Companion.mediaDisplayName
import coil.request.Disposable
import kotlinx.android.synthetic.main.adapter_media.view.*
import kotlinx.coroutines.flow.MutableStateFlow

data class MediaItem(
    val entity: CrunchyMedia?
) : RecyclerItem(entity?.mediaId) {

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
        stateFlow: MutableStateFlow<ClickableItem?>,
        selectionMode: ISupportSelectionMode<Long>?
    ) {
        val binding = AdapterMediaBinding.bind(view)
        disposable = binding.mediaImage.setImageUrl(entity?.screenshotImage)
        binding.mediaDescription.text = entity?.description
        binding.mediaTitle.text = entity?.mediaDisplayName()
        binding.container.setOnClickListener {
            stateFlow.value =
                DefaultClickableItem(
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
        view.container.setOnClickListener(null)
        disposable?.dispose()
        disposable = null
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
        ) = AdapterMediaBinding.inflate(
        layoutInflater, viewGroup, false
        ).let { SupportViewHolder(it.root) }

        internal val DIFFER =
            object : DiffUtil.ItemCallback<CrunchyMedia>() {
                override fun areItemsTheSame(
                    oldItem: CrunchyMedia,
                    newItem: CrunchyMedia
                ): Boolean {
                    return oldItem.mediaId == newItem.mediaId
                }

                override fun areContentsTheSame(
                    oldItem: CrunchyMedia,
                    newItem: CrunchyMedia
                ): Boolean {
                    return oldItem.hashCode() == newItem.hashCode()
                }
            }
    }
}