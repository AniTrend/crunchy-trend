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

package co.anitrend.support.crunchyroll.feature.series.viewmodel.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.support.crunchyroll.data.series.usecase.SeriesDetailUseCaseType
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesDetailQuery
import co.anitrend.support.crunchyroll.feature.series.model.SeriesModel

data class SeriesDetailModelState(
    private val useCase: SeriesDetailUseCaseType
) : ISupportViewModelState<SeriesModel?> {

    private val useCaseResult = MutableLiveData<UserInterfaceState<CrunchySeries?>>()

    override val model =
        Transformations.switchMap(useCaseResult) {
            Transformations.map(it.model) { cs ->
                cs?.let { c ->
                    SeriesModel.fromCrunchySeries(c)
                }
            }
        }

    override val networkState =
        Transformations.switchMap(useCaseResult) { it.networkState }

    override val refreshState =
        Transformations.switchMap(useCaseResult) { it.refreshState }

    operator fun invoke(parameter: CrunchySeriesDetailQuery) {
        val result = useCase(parameter)
        useCaseResult.postValue(result)
    }

    /**
     * Triggers use case to perform a retry operation
     */
    override fun retry() {
        val uiModel = useCaseResult.value
        uiModel?.retry?.invoke()
    }

    /**
     * Triggers use case to perform refresh operation
     */
    override fun refresh() {
        val uiModel = useCaseResult.value
        uiModel?.refresh?.invoke()
    }

    /**
     * Called upon [androidx.lifecycle.ViewModel.onCleared] and should optionally
     * call cancellation of any ongoing jobs.
     *
     * If your use case source is of type [co.anitrend.arch.domain.common.IUseCase]
     * then you could optionally call [co.anitrend.arch.domain.common.IUseCase.onCleared] here
     */
    override fun onCleared() {
        useCase.onCleared()
    }

    fun isEmpty() = model.value == null
}