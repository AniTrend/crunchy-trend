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

package co.anitrend.support.crunchyroll.feature.series.presenter

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.ui.extension.setUpWith
import co.anitrend.arch.ui.recycler.SupportRecyclerView
import co.anitrend.arch.ui.recycler.holder.event.ItemClickListener
import co.anitrend.support.crunchyroll.core.extensions.koinOf
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection
import co.anitrend.support.crunchyroll.feature.series.R
import co.anitrend.support.crunchyroll.feature.series.model.SeriesModel
import co.anitrend.support.crunchyroll.feature.series.ui.adpter.SeriesGenreAdapter
import co.anitrend.support.crunchyroll.feature.series.ui.adpter.SeriesSeasonAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class SeriesDetailPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    fun publisherYear(model: LiveData<SeriesModel?>) : String {
        val separator = "\u2022"
        val seriesModel = model.value
        val year = seriesModel?.year
        if (year != null && year > 0)
            return "${seriesModel.publisher}  $separator  $year"
        return "${seriesModel?.publisher}  $separator  Unknown year"
    }

    fun seriesRating(model: LiveData<SeriesModel?>) : Float {
        val seriesModel = model.value
        val rating = seriesModel?.rating
        if (rating != null && rating > 0)
            return (rating / 100f) * 5
        return 0f
    }

    fun mediaCount(model: LiveData<SeriesModel?>) : String {
        val seriesModel = model.value
        val mediaCount = seriesModel?.mediaCount
        if (mediaCount != null && mediaCount > 0)
            return "$mediaCount"
        return "Unknown"
    }

    fun collectionCount(model: LiveData<SeriesModel?>) : String {
        val seriesModel = model.value
        val collectionCount = seriesModel?.collectionCount
        if (collectionCount != null && collectionCount > 0)
            return "$collectionCount"
        return "Unknown"
    }

    fun setupGenresAdapter(seriesGenreRecycler: SupportRecyclerView?, genreAdapter: SeriesGenreAdapter) {
        seriesGenreRecycler?.setUpWith(
            supportAdapter = genreAdapter,
            vertical = true,
            recyclerLayoutManager = FlexboxLayoutManager(
                seriesGenreRecycler.context
            ).also { layoutManager ->
                layoutManager.flexDirection = FlexDirection.ROW
                layoutManager.justifyContent = JustifyContent.FLEX_START
            }
        )
    }

    fun setupSeasonsAdapter(seriesSeasonRecycler: SupportRecyclerView?, seasonAdapter: SeriesSeasonAdapter) {
        seriesSeasonRecycler?.setUpWith(
            supportAdapter = seasonAdapter,
            recyclerLayoutManager = StaggeredGridLayoutManager(
                context.resources.getInteger(R.integer.single_list_size),
                StaggeredGridLayoutManager.VERTICAL
            )
        )
    }

    fun seasonTitle(season: String): String {
        return "${context.getString(R.string.label_season)} $season"
    }
}