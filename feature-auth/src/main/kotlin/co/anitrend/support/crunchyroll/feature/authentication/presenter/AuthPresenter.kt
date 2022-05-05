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

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import co.anitrend.arch.domain.entities.LoadState
import co.anitrend.arch.extension.ext.empty
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyProperty
import co.anitrend.support.crunchyroll.data.authentication.contract.AccountType
import co.anitrend.support.crunchyroll.data.authentication.contract.TokenType
import co.anitrend.support.crunchyroll.data.util.extension.requireProperty
import co.anitrend.support.crunchyroll.domain.authentication.models.CrunchyLoginQuery
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser
import co.anitrend.support.crunchyroll.feature.authentication.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.flow

class AuthPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    fun onLoginStateChange(
        crunchyUser: CrunchyUser,
        accountManager: AccountManager,
        isNewAccount: Boolean,
        userPassword: String
    ) {
        settings.authenticatedUserId.value = crunchyUser.userId
        settings.hasAccessToPremium.value = !crunchyUser.premium.isNullOrBlank()
        settings.isAuthenticated.value = true
        val account = Account(crunchyUser.username, AccountType.UNIVERSAL.id)
        if (isNewAccount) {
            accountManager.addAccountExplicitly(account, userPassword, Bundle.EMPTY)
            accountManager.setAuthToken(account, TokenType.AUTHENTICATED.name, crunchyUser.token)
        } else {
            accountManager.setPassword(account, userPassword)
            accountManager.setAuthToken(account, TokenType.AUTHENTICATED.name, crunchyUser.token)
        }
    }

    fun onAnonymousRequest(
        accountManager: AccountManager
    ) = flow {
        emit(LoadState.Loading())
        val account = Account(TokenType.ANONYMOUS.alias, AccountType.UNIVERSAL.id)
        accountManager.addAccountExplicitly(account, requireProperty(CrunchyProperty.CLIENT_TOKEN), Bundle.EMPTY)
        accountManager.setAuthToken(account, TokenType.ANONYMOUS.name, String.empty())
        emit(LoadState.Success())
    }

    /**
     * validate form and invoke action on success
     */
    inline fun onSubmit(
        loginQuery: CrunchyLoginQuery,
        binding: FragmentLoginBinding,
        onSuccess: (CrunchyLoginQuery) -> Unit
    ) {
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
            onSuccess(loginQuery)
    }
}