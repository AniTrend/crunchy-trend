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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import java.util.ArrayList

class CatalogPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    private val catalogCache = ArrayList<String>(5)

    fun setUpGroupAdapter(
        catalog: CrunchyCatalogWithSeries,
        groupAdapter: GroupAdapter<*>,
        supportStateLayout: SupportStateLayout
    ) {
        if (!catalog.series.isNullOrEmpty())
            // hack around for now until I figure out how to best update sections
            if (!catalogCache.contains(catalog.qualifier.attribute)) {
                val section = Section(
                    HeaderItem(
                        catalog.qualifier
                    )
                )
                section.setHideWhenEmpty(true)
                val adapter = GroupAdapter<GroupieViewHolder>()
                catalog.series.forEach { series ->
                    adapter.add(
                        CatalogItem(
                            series
                        )
                    )
                }
                val carouselGroup = CarouselGroup(adapter)
                section.add(carouselGroup)

                groupAdapter.add(section)
                supportStateLayout.setNetworkState(
                    NetworkState.Success
                )
                catalogCache.add(catalog.qualifier.attribute)
            }
    }

    fun onRefresh() {
        catalogCache.clear()
    }
}