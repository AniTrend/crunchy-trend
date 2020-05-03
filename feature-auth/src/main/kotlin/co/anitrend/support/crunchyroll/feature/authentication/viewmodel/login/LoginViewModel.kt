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

package co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login

import androidx.lifecycle.ViewModel
import co.anitrend.arch.core.viewmodel.SupportViewModel
import co.anitrend.arch.extension.empty
import co.anitrend.support.crunchyroll.data.authentication.usecase.LoginUseCaseType
import co.anitrend.support.crunchyroll.domain.authentication.models.CrunchyLoginQuery
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login.model.LoginModelState

class LoginViewModel(
    useCase: LoginUseCaseType
) : ViewModel() {

    val loginQuery =
        CrunchyLoginQuery(
            account = String.empty(),
            password = String.empty()
        )

    val state = LoginModelState(useCase)

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    override fun onCleared() {
        state.onCleared()
        super.onCleared()
    }
}