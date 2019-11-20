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

package co.anitrend.support.crunchyroll.feature.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import co.anitrend.arch.core.viewmodel.SupportPagingViewModel
import co.anitrend.support.crunchyroll.data.series.usecase.SeriesSearchUseCaseImpl
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesSearchQuery

class SeriesSearchViewModel(
    override val useCase: SeriesSearchUseCaseImpl
) : SupportPagingViewModel<CrunchySeriesSearchQuery, PagedList<CrunchySeries>>() {

    val searchQueryLiveData: MutableLiveData<CrunchySeriesSearchQuery> = MutableLiveData()
}