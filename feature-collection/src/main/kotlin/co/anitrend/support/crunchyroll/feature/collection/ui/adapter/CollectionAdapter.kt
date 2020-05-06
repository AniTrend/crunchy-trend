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

package co.anitrend.support.crunchyroll.feature.collection.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.anitrend.arch.ui.recycler.adapter.SupportPagedListAdapter
import co.anitrend.arch.ui.recycler.holder.SupportViewHolder
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.arch.ui.util.StateLayoutConfig
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection
import co.anitrend.support.crunchyroll.feature.collection.databinding.AdapterCollectionBinding
import co.anitrend.support.crunchyroll.feature.collection.presenter.CollectionPresenter
import co.anitrend.support.crunchyroll.feature.collection.presenter.CollectionPresenter.Companion.titleWithSeason

class CollectionAdapter(
    override val stateConfig: StateLayoutConfig,
    private val itemClickListener: ItemClickListener<CrunchyCollection>
) : SupportPagedListAdapter<CrunchyCollection>() {

    /**
     * Used to get stable ids for [androidx.recyclerview.widget.RecyclerView.Adapter] but only if
     * [androidx.recyclerview.widget.RecyclerView.Adapter.setHasStableIds] is set to true.
     *
     * The identifiable id of each item should unique, and if non exists
     * then this function should return [androidx.recyclerview.widget.RecyclerView.NO_ID]
     */
    override fun getStableIdFor(item: CrunchyCollection?): Long {
        return item?.collectionId ?: RecyclerView.NO_ID
    }

    /**
     * Should provide the required view holder, this function is a substitute for [onCreateViewHolder] which now
     * has extended functionality
     */
    override fun createDefaultViewHolder(
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): SupportViewHolder<CrunchyCollection> {
        val binding = AdapterCollectionBinding.inflate(
            layoutInflater,
            parent,
            false
        )

        return SeriesSeasonViewHolder(
            binding, itemClickListener
        )
    }

    internal class SeriesSeasonViewHolder(
        private val binding: AdapterCollectionBinding,
        private val clickListener: ItemClickListener<CrunchyCollection>
    ) : SupportViewHolder<CrunchyCollection>(binding.root) {

        private var model: CrunchyCollection? = null

        /**
         * Load images, text, buttons, etc. in this method from the given parameter
         *
         * @param model Is the liveData at the current adapter position
         */
        override fun invoke(model: CrunchyCollection?) {
            this.model = model
            binding.collectionTitle.text = model?.titleWithSeason()
            binding.collectionDescription.text = model?.description
            binding.container.setOnClickListener {
                onItemClick(it, clickListener)
            }
        }

        override fun onViewRecycled() {
            binding.container.setOnClickListener(null)
            model = null
        }

        override fun onItemClick(view: View, itemClickListener: ItemClickListener<CrunchyCollection>) {
            model?.apply {
                performClick(this, view, itemClickListener)
            }
        }
    }
}