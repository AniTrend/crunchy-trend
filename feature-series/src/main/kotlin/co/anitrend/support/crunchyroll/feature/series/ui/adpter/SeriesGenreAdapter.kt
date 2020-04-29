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

package co.anitrend.support.crunchyroll.feature.series.ui.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.anitrend.arch.ui.recycler.adapter.SupportListAdapter
import co.anitrend.arch.ui.recycler.holder.SupportViewHolder
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.support.crunchyroll.feature.series.databinding.AdapterGenreBinding

class SeriesGenreAdapter(
    private val itemClickListener: ItemClickListener<String>,
    override val stateConfiguration: SupportStateLayoutConfiguration
) : SupportListAdapter<String>() {

    /**
     * Used to get stable ids for [androidx.recyclerview.widget.RecyclerView.Adapter] but only if
     * [androidx.recyclerview.widget.RecyclerView.Adapter.setHasStableIds] is set to true.
     *
     * The identifiable id of each item should unique, and if non exists
     * then this function should return [androidx.recyclerview.widget.RecyclerView.NO_ID]
     */
    override fun getStableIdFor(item: String?): Long {
        return item?.hashCode()?.toLong() ?: RecyclerView.NO_ID
    }

    /**
     * Should provide the required view holder, this function is a substitute for [onCreateViewHolder] which now
     * has extended functionality
     */
    override fun createDefaultViewHolder(
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): SupportViewHolder<String> {
        return SeriesGenreViewHolder(
            AdapterGenreBinding.inflate(
                layoutInflater, parent, false
            ),
            itemClickListener
        )
    }

    internal class SeriesGenreViewHolder(
        private val binding: AdapterGenreBinding,
        private val listener: ItemClickListener<String>
    ) : SupportViewHolder<String>(binding.root) {

        /**
         * Load images, text, buttons, etc. in this method from the given parameter
         *
         * @param model Is the liveData at the current adapter position
         */
        override fun invoke(model: String?) {
            binding.entity = model
            binding.seriesGenre.setOnClickListener {
                onItemClick(it, listener)
            }
            binding.executePendingBindings()
        }

        /**
         * If any image views are used within the view holder, clear any pending async requests
         * by using [com.bumptech.glide.RequestManager.clear]
         *
         * @see com.bumptech.glide.Glide
         */
        override fun onViewRecycled() {
            binding.seriesGenre.setOnClickListener(null)
            binding.unbind()
        }

        /**
         * Handle any onclick events from our views, optionally you can call
         * [performClick] to dispatch [Pair]<[Int], T> on the [ItemClickListener]
         *
         * @param view the view that has been clicked
         * @param itemClickListener callback for handing clicks
         */
        override fun onItemClick(
            view: View,
            itemClickListener: ItemClickListener<String>
        ) = performClick(binding.entity, view, itemClickListener)
    }
}