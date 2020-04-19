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

package co.anitrend.support.crunchyroll.feature.news.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.anitrend.arch.ui.recycler.adapter.SupportPagedListAdapter
import co.anitrend.arch.ui.recycler.holder.SupportViewHolder
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews
import co.anitrend.support.crunchyroll.feature.news.databinding.AdapterNewsFeedBinding
import io.noties.markwon.Markwon

class RssNewsAdapter(
    private val markwon: Markwon,
    override val stateConfiguration: SupportStateLayoutConfiguration,
    private val itemClickListener: ItemClickListener<CrunchyNews>
) : SupportPagedListAdapter<CrunchyNews>() {


    /**
     * Used to get stable ids for [androidx.recyclerview.widget.RecyclerView.Adapter] but only if
     * [androidx.recyclerview.widget.RecyclerView.Adapter.setHasStableIds] is set to true.
     *
     * The identifiable id of each item should unique, and if non exists
     * then this function should return [androidx.recyclerview.widget.RecyclerView.NO_ID]
     */
    override fun getStableIdFor(item: CrunchyNews?): Long {
        return item?.title?.hashCode()?.toLong() ?: RecyclerView.NO_ID
    }

    /**
     * Should provide the required view holder, this function is a substitute for [onCreateViewHolder] which now
     * has extended functionality
     */
    override fun createDefaultViewHolder(
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): SupportViewHolder<CrunchyNews> {
        val binding = AdapterNewsFeedBinding.inflate(
            layoutInflater,
            parent,
            false
        )

        return NewsRssViewHolder(itemClickListener, binding, markwon)
    }

    internal class NewsRssViewHolder(
        private val clickListener: ItemClickListener<CrunchyNews>,
        private val binding: AdapterNewsFeedBinding,
        private val markwon: Markwon
    ): SupportViewHolder<CrunchyNews>(binding.root) {

        /**
         * Load images, text, buttons, etc. in this method from the given parameter
         *
         * @param model Is the liveData at the current adapter position
         */
        override fun invoke(model: CrunchyNews?) {
            binding.entity = model
            markwon.setMarkdown(
                binding.mediaNewsDescription,
                model?.description ?: "No description available"
            )
            binding.container.setOnClickListener {
                onItemClick(it, clickListener)
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
            binding.container.setOnClickListener(null)
            binding.unbind()
        }

        /**
         * Handle any onclick events from our views, optionally you can call
         * [performClick] to dispatch [Pair]<[Int], T> on the [ItemClickListener]
         *
         * @param view the view that has been clicked
         */
        override fun onItemClick(view: View, itemClickListener: ItemClickListener<CrunchyNews>) {
            performClick(binding.entity, view, itemClickListener)
        }
    }
}