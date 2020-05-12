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

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import co.anitrend.arch.extension.gone
import co.anitrend.arch.recycler.common.ClickableItem
import co.anitrend.arch.recycler.common.DefaultClickableItem
import co.anitrend.arch.recycler.holder.SupportViewHolder
import co.anitrend.arch.recycler.model.RecyclerItem
import co.anitrend.arch.ui.extension.setUpWith
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.feature.catalog.R
import co.anitrend.support.crunchyroll.feature.catalog.databinding.AdapterCatalogBinding
import co.anitrend.support.crunchyroll.shared.series.adapter.SeriesGridViewAdapter
import kotlinx.android.synthetic.main.adapter_catalog.view.*
import kotlinx.android.synthetic.main.item_carousel.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import java.util.*

data class CatalogItem(
    val entity: CrunchyCatalogWithSeries?,
    private val scope: LifecycleCoroutineScope
) : RecyclerItem(entity?.qualifier?.ordinal?.toLong()) {

    //private var catalogSeriesRecycler: SupportRecyclerView? = null
    private var job: Job? = null

    private fun setUpCatalogItems(
        view: View,
        clickObservable: MutableLiveData<ClickableItem>
    ) {
        val catalogSeriesAdapter = SeriesGridViewAdapter(
            view.resources,
            stateConfiguration = StateLayoutConfig(
                retryAction = R.string.label_text_action_retry
            )
        )
        val catalogSeriesRecycler = view.carouselRecycler
        catalogSeriesRecycler?.setUpWith(
            supportAdapter = catalogSeriesAdapter,
            recyclerLayoutManager = LinearLayoutManager(
                view.context, LinearLayoutManager.HORIZONTAL, false
            )
        )
        catalogSeriesAdapter.submitList(entity?.series as List)
        job = scope.launch {
            catalogSeriesAdapter.clickableFlow
                .filterIsInstance<DefaultClickableItem<CrunchySeries>>()
                .collect {
                    val model = it.data
                    val seriesPayload = NavigationTargets.Series.Payload(
                        seriesId = model?.seriesId ?: 0
                    )
                    NavigationTargets.Series(
                        it.view.context, seriesPayload
                    )
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
     * @param clickObservable observable to broadcast click events
     */
    @ExperimentalStdlibApi
    override fun bind(
        view: View,
        position: Int,
        payloads: List<Any>,
        clickObservable: MutableLiveData<ClickableItem>
    ) {
        val binding = AdapterCatalogBinding.bind(view)
        binding.catalogHeadingTitle.text = entity?.qualifier?.attribute?.capitalize(Locale.getDefault())
        binding.catalogActionSeeAll.gone()
        // too much effort to implement so leaving this out for now
        /*binding.catalogActionSeeAll.setOnClickListener {
            Toast.makeText(view.context, "Not yet supported.. :smirk", Toast.LENGTH_SHORT).show()
        }*/
        setUpCatalogItems(view, clickObservable)
    }

    /**
     * Called when the view needs to be recycled for reuse, clear any held references
     * to objects, stop any asynchronous work, e.t.c
     */
    override fun unbind(view: View) {
        view.catalogActionSeeAll.setOnClickListener(null)
        //catalogSeriesRecycler?.adapter = null
        //catalogSeriesRecycler = null
        job?.cancel()
        job = null
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
        ) = AdapterCatalogBinding.inflate(
        layoutInflater, viewGroup, false
        ).let { SupportViewHolder(it.root) }

        internal val DIFFER =
            object : DiffUtil.ItemCallback<CrunchyCatalogWithSeries>() {
                override fun areItemsTheSame(
                    oldItem: CrunchyCatalogWithSeries,
                    newItem: CrunchyCatalogWithSeries
                ): Boolean {
                    return oldItem.qualifier == newItem.qualifier
                }

                override fun areContentsTheSame(
                    oldItem: CrunchyCatalogWithSeries,
                    newItem: CrunchyCatalogWithSeries
                ): Boolean {
                    return oldItem.hashCode() == newItem.hashCode()
                }
            }
    }
}