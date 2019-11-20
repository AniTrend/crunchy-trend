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

package co.anitrend.support.crunchyroll.feature.authentication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import co.anitrend.arch.core.viewmodel.SupportViewModel
import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.support.crunchyroll.data.authentication.usecase.LoginUseCaseImpl
import co.anitrend.support.crunchyroll.data.authentication.usecase.LogoutUseCaseImpl
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser

class LogoutViewModel(
    override val useCase: LogoutUseCaseImpl,
    private val loginUseCase: LoginUseCaseImpl
) : SupportViewModel<Nothing?, Boolean>() {

    /**
     * Starts view model operations
     *
     * @param parameter request payload
     */
    override fun invoke(parameter: Nothing?) {
        val result = useCase(parameter)
        useCaseResult.value = result
    }

    fun getCurrentUser(): LiveData<CrunchyUser?> {
        val result = loginUseCase.getLoggedInUser()
        val loginUseCaseResult = MutableLiveData<UserInterfaceState<CrunchyUser?>>()
        loginUseCaseResult.value = result

        return Transformations.switchMap(
            loginUseCaseResult
        ) { it.model }
    }
}