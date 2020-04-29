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

package co.anitrend.support.crunchyroll.feature.catalog.presenter

import android.content.Context
import android.util.SparseArray
import androidx.core.util.contains
import androidx.core.util.forEach
import androidx.lifecycle.LiveData
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.arch.ui.view.widget.SupportStateLayout
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter
import co.anitrend.support.crunchyroll.feature.catalog.controller.group.CarouselGroup
import co.anitrend.support.crunchyroll.feature.catalog.controller.items.CatalogItem
import co.anitrend.support.crunchyroll.feature.catalog.controller.items.HeaderItem
import co.anitrend.support.crunchyroll.feature.catalog.controller.items.PlaceHolderItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import java.util.ArrayList

class CatalogPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    private val sectionMap = SparseArray<Section>(5)
    private val carouselMap = SparseArray<CarouselGroup>(5)

    private fun CrunchyCatalogWithSeries.key() = qualifier.ordinal

    private fun updateGroupAdapter(
        groupAdapter: GroupAdapter<*>
    ) {
        val sections = ArrayList<Section>(sectionMap.size())
        sectionMap.forEach { _, value ->
            sections.add(value)
        }
        groupAdapter.update(sections)
    }

    private fun updateSectionAdapter(
        catalog: CrunchyCatalogWithSeries
    ) {
        val carousels = ArrayList<CarouselGroup>(carouselMap.size())
        carouselMap.forEach { _, value ->
            carousels.add(value)
        }
        sectionMap[catalog.key()].update(carousels)
    }

    fun updatePlaceHolderState(
        onRetry: () -> Unit,
        networkState: NetworkState,
        catalog: CrunchyCatalogWithSeries?,
        groupAdapter: GroupAdapter<*>
    ) {
        if (catalog != null && sectionMap.contains(catalog.key())) {
            sectionMap[catalog.key()].setPlaceholder(
                PlaceHolderItem(networkState, onRetry)
            )
            updateGroupAdapter(groupAdapter)
        }
    }

    private fun createCarouselGroup(
        catalog: CrunchyCatalogWithSeries
    ): CarouselGroup {
        val adapter = GroupAdapter<GroupieViewHolder>()
        for (series in catalog.series) {
            val catalogItem = CatalogItem(series)
            adapter.add(catalogItem)
        }
        return CarouselGroup(adapter)
    }

    fun setUpGroupAdapter(
        catalog: CrunchyCatalogWithSeries,
        groupAdapter: GroupAdapter<*>
    ) {
        // hack around for now until I figure out how to best update sections
        if (!sectionMap.contains(catalog.key())) {
            val section = Section(
                HeaderItem(catalog.qualifier)
            )

            section.setPlaceholder(
                PlaceHolderItem(NetworkState.Loading)
            )

            val carouselGroup = createCarouselGroup(catalog)
            carouselMap.put(catalog.key(), carouselGroup)

            section.add(carouselGroup)
            sectionMap.put(catalog.key(), section)

            groupAdapter.add(sectionMap[catalog.key()])
        } else {
            val carouselGroup = createCarouselGroup(catalog)
            carouselMap.put(catalog.key(), carouselGroup)
            updateSectionAdapter(catalog)
            updateGroupAdapter(groupAdapter)
        }
    }
}