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

package co.anitrend.support.crunchyroll.feature.catalog.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.data.catalog.usecase.CatalogUseCaseImpl
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import co.anitrend.support.crunchyroll.domain.catalog.models.CrunchyCatalogQuery

data class CatalogViewState(
    private val parameter: CrunchyCatalogQuery,
    private val useCase: CatalogUseCaseImpl
) {
    private val useCaseResult = MutableLiveData<UserInterfaceState<CrunchyCatalogWithSeries>>()

    val model =
        Transformations.switchMap(useCaseResult) { it.model }

    val networkState: LiveData<NetworkState>? =
        Transformations.switchMap(useCaseResult) { it.networkState }

    val refreshState: LiveData<NetworkState>? =
        Transformations.switchMap(useCaseResult) { it.refreshState }

    operator fun invoke() {
        val result = useCase(parameter)
        useCaseResult.postValue(result)
    }

    /**
     * Requests the repository to perform a retry operation
     */
    fun retry() {
        val uiModel = useCaseResult.value
        uiModel?.retry?.invoke()
    }

    /**
     * Requests the repository to perform a refreshAndInvalidate operation on the underlying database
     */
    fun refresh() {
        val uiModel = useCaseResult.value
        uiModel?.refresh?.invoke()
    }

    fun hasData() = model.value != null
}