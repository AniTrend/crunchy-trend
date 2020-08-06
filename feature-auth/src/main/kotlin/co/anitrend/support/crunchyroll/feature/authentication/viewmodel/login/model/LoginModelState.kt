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

package co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asLiveData
import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.data.state.DataState
import co.anitrend.support.crunchyroll.data.authentication.usecase.LoginUseCaseType
import co.anitrend.support.crunchyroll.domain.authentication.models.CrunchyLoginQuery
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser

data class LoginModelState(
    private val useCase: LoginUseCaseType
) : ISupportViewModelState<CrunchyUser?> {

    private val useCaseResult = MutableLiveData<DataState<CrunchyUser?>>()

    override val model =
        Transformations.switchMap(useCaseResult) { it.model }

    override val networkState =
        Transformations.switchMap(useCaseResult) { it.networkState.asLiveData() }

    override val refreshState =
        Transformations.switchMap(useCaseResult) { it.refreshState.asLiveData() }

    /**
     * Authenticates the user using the supplied credentials
     */
    operator fun invoke(parameter: CrunchyLoginQuery) {
        val result = useCase(parameter)
        useCaseResult.postValue(result)
    }

    /**
     * Returns authenticated user
     */
    operator fun invoke() {
        val result = useCase.getLoggedInUser()
        useCaseResult.postValue(result)
    }

    /**
     * Triggers use case to perform a retry operation
     */
    override suspend fun retry() {
        val uiModel = useCaseResult.value
        uiModel?.retry?.invoke()
    }

    /**
     * Triggers use case to perform refresh operation
     */
    override suspend fun refresh() {
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
}