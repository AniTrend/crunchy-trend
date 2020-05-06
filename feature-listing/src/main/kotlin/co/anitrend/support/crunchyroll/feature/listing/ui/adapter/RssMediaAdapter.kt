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

package co.anitrend.support.crunchyroll.feature.listing.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.anitrend.arch.ui.recycler.adapter.SupportPagedListAdapter
import co.anitrend.arch.ui.recycler.holder.SupportViewHolder
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.domain.episode.entities.CrunchyEpisodeFeed
import co.anitrend.support.crunchyroll.feature.feed.databinding.AdapterMediaFeedBinding
import coil.request.RequestDisposable

class RssMediaAdapter(
    override val stateConfig: StateLayoutConfig,
    private val itemClickListener: ItemClickListener<CrunchyEpisodeFeed>
) : SupportPagedListAdapter<CrunchyEpisodeFeed>() {

    /**
     * Used to get stable ids for [androidx.recyclerview.widget.RecyclerView.Adapter] but only if
     * [androidx.recyclerview.widget.RecyclerView.Adapter.setHasStableIds] is set to true.
     *
     * The identifiable id of each item should unique, and if non exists
     * then this function should return [androidx.recyclerview.widget.RecyclerView.NO_ID]
     */
    override fun getStableIdFor(item: CrunchyEpisodeFeed?): Long {
        return item?.id ?: RecyclerView.NO_ID
    }

    /**
     * Should provide the required view holder, this function is a substitute for [onCreateViewHolder] which now
     * has extended functionality
     */
    override fun createDefaultViewHolder(
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): SupportViewHolder<CrunchyEpisodeFeed> {
        val binding = AdapterMediaFeedBinding.inflate(
            layoutInflater,
            parent,
            false
        )

        return MediaRssViewHolder(itemClickListener, binding)
    }


    internal class MediaRssViewHolder(
        private val clickListener: ItemClickListener<CrunchyEpisodeFeed>,
        private val binding: AdapterMediaFeedBinding
    ): SupportViewHolder<CrunchyEpisodeFeed>(binding.root) {

        private var model: CrunchyEpisodeFeed? = null
        private var disposable: RequestDisposable? = null

        /**
         * Load images, text, buttons, etc. in this method from the given parameter
         *
         * @param model Is the liveData at the current adapter position
         */
        override fun invoke(model: CrunchyEpisodeFeed?) {
            this.model = model
            disposable = binding.mediaThumbnail.setImageUrl(model)
            binding.mediaTitle.text = model?.title
            binding.mediaDuration.text = model?.episodeDuration
            binding.mediaThumbnail.setOnClickListener {
                onItemClick(it, clickListener)
            }
        }

        override fun onViewRecycled() {
            binding.mediaThumbnail.setOnClickListener(null)
            disposable?.dispose()
            disposable = null
            model = null
        }

        /**
         * Handle any onclick events from our views, optionally you can call
         * [performClick] to dispatch [Pair]<[Int], T> on the [ItemClickListener]
         *
         * @param view the view that has been clicked
         */
        override fun onItemClick(view: View, itemClickListener: ItemClickListener<CrunchyEpisodeFeed>) {
            model.apply {
                performClick(this, view, itemClickListener)
            }
        }
    }
}