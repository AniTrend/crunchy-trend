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

package co.anitrend.support.crunchyroll.feature.catalog.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.support.crunchyroll.data.catalog.usecase.CatalogUseCaseImpl
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter
import co.anitrend.support.crunchyroll.domain.catalog.models.CrunchyCatalogQuery
import co.anitrend.support.crunchyroll.feature.catalog.model.CatalogViewState

class CatalogViewModel(
    val catalogUseCase: CatalogUseCaseImpl
) : ViewModel() {

    val mediatorLiveData = MediatorLiveData<List<CrunchyCatalogWithSeries>>()

    val viewStateFeatured by lazy(LAZY_MODE_UNSAFE) {
        CatalogViewState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.FEATURED
            ),
            catalogUseCase
        )
    }

    val viewStateNewest by lazy(LAZY_MODE_UNSAFE) {
        CatalogViewState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.NEWEST
            ),
            catalogUseCase
        )
    }

    val viewStatePopular by lazy(LAZY_MODE_UNSAFE) {
        CatalogViewState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.POPULAR
            ),
            catalogUseCase
        )
    }

    val viewStateSimulcast by lazy(LAZY_MODE_UNSAFE) {
        CatalogViewState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.SIMULCAST
            ),
            catalogUseCase
        )
    }

    val viewStateUpdated by lazy(LAZY_MODE_UNSAFE) {
        CatalogViewState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.UPDATED
            ),
            catalogUseCase
        )
    }

    init {
        mediatorLiveData.addSource(viewStateFeatured.model) { }
        mediatorLiveData.addSource(viewStateNewest.model) { }
        mediatorLiveData.addSource(viewStatePopular.model) { }
        mediatorLiveData.addSource(viewStateSimulcast.model) { }
        mediatorLiveData.addSource(viewStateUpdated.model) { }
    }

    fun hasModelData() = !mediatorLiveData.value.isNullOrEmpty()
}