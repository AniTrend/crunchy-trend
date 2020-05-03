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

import androidx.lifecycle.ViewModel
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.support.crunchyroll.data.catalog.usecase.CatalogUseCaseType
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter
import co.anitrend.support.crunchyroll.domain.catalog.models.CrunchyCatalogQuery
import co.anitrend.support.crunchyroll.feature.catalog.model.CatalogViewModelState

class CatalogViewModel(
    catalogUseCase: CatalogUseCaseType
) : ViewModel() {

    private val viewStateFeatured =
        CatalogViewModelState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.FEATURED
            ),
            catalogUseCase
        )

    private val viewStateNewest =
        CatalogViewModelState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.NEWEST
            ),
            catalogUseCase
        )

    private val viewStatePopular =
        CatalogViewModelState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.POPULAR
            ),
            catalogUseCase
        )

    private val viewStateSimulcast =
        CatalogViewModelState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.SIMULCAST
            ),
            catalogUseCase
        )

    private val viewStateUpdated =
        CatalogViewModelState(
            CrunchyCatalogQuery(
                CrunchySeriesCatalogFilter.UPDATED
            ),
            catalogUseCase
        )


    val viewModelLists by lazy {
        listOf(
            viewStateFeatured,
            viewStateNewest,
            viewStatePopular,
            viewStateSimulcast,
            viewStateUpdated
        )
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    override fun onCleared() {
        viewModelLists.forEach {
            it.onCleared()
        }
        super.onCleared()
    }
}