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

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.anitrend.arch.core.model.IStateLayoutConfig
import co.anitrend.arch.recycler.action.contract.ISupportSelectionMode
import co.anitrend.arch.recycler.adapter.SupportListAdapter
import co.anitrend.arch.recycler.model.contract.IRecyclerItem
import co.anitrend.arch.theme.animator.contract.AbstractAnimator
import co.anitrend.support.crunchyroll.feature.series.controller.model.GenreItem

class SeriesGenreAdapter(
    override val resources: Resources,
    override val stateConfiguration: IStateLayoutConfig,
    override val customSupportAnimator: AbstractAnimator? = null,
    override val mapper: (String?) -> IRecyclerItem = { GenreItem(it) }
) : SupportListAdapter<String>(GenreItem.DIFFER) {

    /**
     * Assigned if the current adapter supports needs to supports action mode
     */
    override var supportAction: ISupportSelectionMode<Long>? = null

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
     * Should provide the required view holder,
     * this function is a substitute for [onCreateViewHolder] which now
     * has extended functionality
     */
    override fun createDefaultViewHolder(
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ) = GenreItem.createViewHolder(parent, layoutInflater)
}