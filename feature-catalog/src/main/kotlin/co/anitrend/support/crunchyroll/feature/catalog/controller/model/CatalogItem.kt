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

package co.anitrend.support.crunchyroll.feature.catalog.controller.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.anitrend.arch.extension.ext.gone
import co.anitrend.arch.recycler.action.contract.ISupportSelectionMode
import co.anitrend.arch.recycler.common.ClickableItem
import co.anitrend.arch.recycler.holder.SupportViewHolder
import co.anitrend.arch.ui.extension.setUpWith
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.android.recycler.model.RecyclerItemBinding
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.feature.catalog.R
import co.anitrend.support.crunchyroll.feature.catalog.databinding.AdapterCatalogBinding
import co.anitrend.support.crunchyroll.navigation.Series
import co.anitrend.support.crunchyroll.shared.series.adapter.SeriesGridViewAdapter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance

data class CatalogItem(
    val entity: CrunchyCatalogWithSeries?
) : RecyclerItemBinding<AdapterCatalogBinding>(
    entity?.qualifier?.ordinal?.toLong() ?: 0
), CoroutineScope by MainScope() {

    private fun setUpCatalogItems(view: View) {
        val catalogSeriesAdapter = SeriesGridViewAdapter(
            view.resources,
            stateConfiguration = StateLayoutConfig(
                retryAction = R.string.label_text_action_retry
            )
        )
        val catalogSeriesRecycler = requireBinding().catalogItems.carouselRecycler
        val animator = object : DefaultItemAnimator() {
            override fun getSupportsChangeAnimations() = false
        }
        animator.supportsChangeAnimations = false
        catalogSeriesRecycler.itemAnimator = animator
        catalogSeriesRecycler.setUpWith(
            supportAdapter = catalogSeriesAdapter,
            recyclerLayoutManager = LinearLayoutManager(
                view.context, LinearLayoutManager.HORIZONTAL, false
            )
        )
        catalogSeriesAdapter.submitList(entity?.series as List)
        launch(Dispatchers.Default) {
            catalogSeriesAdapter.clickableFlow.debounce(DEBOUNCE_DURATION)
                .filterIsInstance<ClickableItem.Data<CrunchySeries>>()
                .collect {
                    val data = it.data

                    val payload = Series.Payload(
                        seriesId = data.seriesId
                    )

                    Series(it.view.context, payload)
                }
        }
    }

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
        binding = AdapterCatalogBinding.bind(view)
        requireBinding().catalogHeadingTitle.text = entity?.qualifier?.attribute?.capitalize()
        requireBinding().catalogActionSeeAll.gone()
        // too much effort to implement so leaving this out for now
        /*binding.catalogActionSeeAll.setOnClickListener {
            Toast.makeText(view.context, "Not yet supported.. :smirk", Toast.LENGTH_SHORT).show()
        }*/
        setUpCatalogItems(view)
    }

    /**
     * Called when the view needs to be recycled for reuse, clear any held references
     * to objects, stop any asynchronous work, e.t.c
     */
    override fun unbind(view: View) {
        binding?.catalogActionSeeAll?.setOnClickListener(null)
        //catalogSeriesRecycler?.adapter = null
        //catalogSeriesRecycler = null
        cancel()
        super.unbind(view)
    }

    companion object {
        internal fun createViewHolder(
            viewGroup: ViewGroup,
            layoutInflater: LayoutInflater
        ) = AdapterCatalogBinding.inflate(
        layoutInflater, viewGroup, false
        ).let { SupportViewHolder(it) }

        internal val DIFFER =
            object : DiffUtil.ItemCallback<CrunchyCatalogWithSeries>() {
                override fun areItemsTheSame(
                    oldItem: CrunchyCatalogWithSeries,
                    newItem: CrunchyCatalogWithSeries
                ): Boolean {
                    return oldItem.qualifier == newItem.qualifier &&
                            oldItem.series == newItem.series
                }

                override fun areContentsTheSame(
                    oldItem: CrunchyCatalogWithSeries,
                    newItem: CrunchyCatalogWithSeries
                ): Boolean {
                    return oldItem.qualifier == newItem.qualifier &&
                            oldItem.series.size == newItem.series.size
                }

                override fun getChangePayload(
                    oldItem: CrunchyCatalogWithSeries,
                    newItem: CrunchyCatalogWithSeries
                ): Any? {
                    return super.getChangePayload(oldItem, newItem)
                }
            }
    }
}