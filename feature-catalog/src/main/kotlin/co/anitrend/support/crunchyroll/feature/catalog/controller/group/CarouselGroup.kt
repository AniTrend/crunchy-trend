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

package co.anitrend.support.crunchyroll.feature.catalog.controller.group

import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.*
import co.anitrend.support.crunchyroll.feature.catalog.controller.CatalogCarouselContainer

class CarouselGroup(
    adapter: GroupAdapter<GroupieViewHolder>
) : Group {

    private var isEmpty = true
    private val adapter: RecyclerView.Adapter<*>
    private var groupDataObserver: GroupDataObserver? = null
    private val carouselItem: CatalogCarouselContainer

    init {
        this.adapter = adapter
        carouselItem = CatalogCarouselContainer(adapter = adapter)
        isEmpty = adapter.itemCount == 0
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                val empty = adapter.itemCount == 0
                if (empty && !isEmpty) {
                    isEmpty = empty
                    groupDataObserver?.onItemRemoved(carouselItem, 0)
                }
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                val empty = adapter.itemCount == 0
                if (isEmpty && !empty) {
                    isEmpty = empty
                    groupDataObserver?.onItemInserted(carouselItem, 0)
                }
            }
        })
    }



    override fun getItemCount(): Int = if (isEmpty) 0 else 1

    override fun getItem(position: Int): Item<*> =
        if (position == 0 && !isEmpty) carouselItem else throw IndexOutOfBoundsException()

    override fun getPosition(item: Item<*>): Int = if (item === carouselItem && !isEmpty) 0 else -1

    override fun registerGroupDataObserver(groupDataObserver: GroupDataObserver) {
        this.groupDataObserver = groupDataObserver
    }

    override fun unregisterGroupDataObserver(groupDataObserver: GroupDataObserver) {
        this.groupDataObserver = null
    }
}