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

package co.anitrend.support.crunchyroll.feature.search.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.anitrend.arch.ui.recycler.adapter.SupportPagedListAdapter
import co.anitrend.arch.ui.recycler.holder.SupportViewHolder
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.feature.search.databinding.AdapterSeriesBinding
import coil.request.RequestDisposable

class SeriesViewAdapter(
    override val stateConfig: StateLayoutConfig,
    private val itemClickListener: ItemClickListener<CrunchySeries>
) : SupportPagedListAdapter<CrunchySeries>() {

    /**
     * Used to get stable ids for [androidx.recyclerview.widget.RecyclerView.Adapter] but only if
     * [androidx.recyclerview.widget.RecyclerView.Adapter.setHasStableIds] is set to true.
     *
     * The identifiable id of each item should unique, and if non exists
     * then this function should return [androidx.recyclerview.widget.RecyclerView.NO_ID]
     */
    override fun getStableIdFor(item: CrunchySeries?): Long {
        return item?.seriesId ?: RecyclerView.NO_ID
    }

    /**
     * Should provide the required view holder, this function is a substitute for [onCreateViewHolder] which now
     * has extended functionality
     */
    override fun createDefaultViewHolder(
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): SupportViewHolder<CrunchySeries> {
        val binding = AdapterSeriesBinding.inflate(
                layoutInflater,
                parent,
                false
        )

        return SeriesViewHolder(
            binding, itemClickListener
        )
    }

    internal class SeriesViewHolder(
        private val binding: AdapterSeriesBinding,
        private var clickListener: ItemClickListener<CrunchySeries>?
    ) : SupportViewHolder<CrunchySeries>(binding.root) {

        private var disposable: RequestDisposable? = null
        private var model: CrunchySeries? = null

        /**
         * Load images, text, buttons, etc. in this method from the given parameter
         *
         * @param model Is the liveData at the current adapter position
         */
        override fun invoke(model: CrunchySeries?) {
            this.model = model
            disposable = binding.seriesImage.setImageUrl(model?.portraitImage)
            binding.seriesName.text = model?.name
            binding.seriesDescription.text = model?.description
            clickListener?.apply {
                binding.container.setOnClickListener {
                    onItemClick(it, this)
                }
            }
        }

        override fun onViewRecycled() {
            binding.container.setOnClickListener(null)
            clickListener = null
            disposable?.dispose()
            disposable = null
        }

        /**
         * Handle any onclick events from our views, optionally you can call
         * [performClick] to dispatch [Pair]<[Int], T> on the [ItemClickListener]
         *
         * @param view the view that has been clicked
         */
        override fun onItemClick(view: View, itemClickListener: ItemClickListener<CrunchySeries>) {
            model.apply {
                performClick(this, view, itemClickListener)
            }
        }
    }
}