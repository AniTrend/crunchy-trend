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

package co.anitrend.support.crunchyroll.feature.catalog.controller

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.anitrend.support.crunchyroll.feature.catalog.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder
import co.anitrend.support.crunchyroll.feature.catalog.databinding.ItemCarouselBinding

class CatalogContainer(
    private val itemDecoration: RecyclerView.ItemDecoration? = null,
    private val adapter: GroupAdapter<*>
) : BindableItem<ItemCarouselBinding>(), OnItemClickListener {

    override fun getLayout() = R.layout.item_carousel

    override fun createViewHolder(itemView: View): GroupieViewHolder<ItemCarouselBinding> {
        val viewHolder =
            super.createViewHolder(itemView)
        val recyclerView: RecyclerView = viewHolder.binding.carouselRecycler
        if (itemDecoration != null)
            recyclerView.addItemDecoration(itemDecoration)
        recyclerView.layoutManager = LinearLayoutManager(
            recyclerView.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        return viewHolder
    }

    /**
     * Perform any actions required to set up the view for display.
     *
     * @param viewBinding The ViewDataBinding to bind
     * @param position The adapter position
     */
    override fun bind(viewBinding: ItemCarouselBinding, position: Int) {
        viewBinding.carouselRecycler.adapter = adapter
    }

    override fun onItemClick(item: Item<*>, view: View) {
        TODO("Not yet implemented")
    }
}