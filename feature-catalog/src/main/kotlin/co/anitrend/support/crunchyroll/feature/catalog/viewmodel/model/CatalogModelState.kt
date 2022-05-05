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

package co.anitrend.support.crunchyroll.feature.catalog.viewmodel.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.data.state.DataState
import co.anitrend.arch.domain.entities.LoadState
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.data.catalog.usecase.CatalogUseCaseType
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries

data class CatalogModelState(
    private val useCase: CatalogUseCaseType
) : ISupportViewModelState<List<CrunchyCatalogWithSeries>> {

    private val useCaseResult = MutableLiveData<DataState<List<CrunchyCatalogWithSeries>>>()

    override val model =
        Transformations.switchMap(useCaseResult) { it.model.asLiveData() }

    override val loadState =
        Transformations.switchMap(useCaseResult) { it.loadState.asLiveData() }

    override val refreshState: LiveData<LoadState> =
        Transformations.switchMap(useCaseResult) { it.refreshState.asLiveData() }

    operator fun invoke() {
        val result = useCase()
        useCaseResult.postValue(result)
    }

    override suspend fun retry() {
        val uiModel = useCaseResult.value
        uiModel?.retry?.invoke()
    }

    override suspend fun refresh() {
        val uiModel = useCaseResult.value
        uiModel?.refresh?.invoke()
    }

    override fun onCleared() {
        useCase.onCleared()
    }
}