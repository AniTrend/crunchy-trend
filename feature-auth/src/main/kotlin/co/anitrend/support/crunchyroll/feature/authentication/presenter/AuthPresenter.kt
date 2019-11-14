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

package co.anitrend.support.crunchyroll.feature.authentication.presenter

import android.content.Context
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.domain.authentication.models.CrunchyLoginQuery
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser
import co.anitrend.support.crunchyroll.feature.authentication.databinding.FragmentLoginBinding

class AuthPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    fun onLoginStateChange(crunchyUser: CrunchyUser?) {
        supportPreference.authenticatedUserId = crunchyUser?.userId ?: IAuthenticationSettings.INVALID_USER_ID
        supportPreference.hasAccessToPremium = crunchyUser?.premium != null
        supportPreference.isAuthenticated = crunchyUser != null
    }

    /**
     * validate form and invoke action on success
     */
    fun onSubmit(loginQuery: CrunchyLoginQuery, binding: FragmentLoginBinding, onSuccess: () -> Unit) {
        var errors = 0
        if (loginQuery.account.isBlank()) {
            binding.userEmailInputLayout.error = "Field cannot be empty"
            errors += 1
        }
        if (loginQuery.password.isBlank()) {
            binding.userPasswordInputLayout.error = "Field cannot be empty"
            errors += 1
        }

        if (errors == 0)
            onSuccess()
    }
}