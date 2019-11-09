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

package co.anitrend.support.crunchyroll.data.repository.authentication

import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.arch.data.repository.SupportRepository
import co.anitrend.support.crunchyroll.data.datasource.auto.authentication.contract.LoginSource
import co.anitrend.support.crunchyroll.data.datasource.auto.authentication.contract.LogoutSource
import co.anitrend.support.crunchyroll.domain.entities.query.authentication.LoginQuery
import co.anitrend.support.crunchyroll.domain.entities.result.user.User
import co.anitrend.support.crunchyroll.domain.repositories.authentication.ILoginRepository
import co.anitrend.support.crunchyroll.domain.repositories.authentication.ILogoutRepository

class AuthenticationRepository(
    private val loginSource: LoginSource,
    private val logoutSource: LogoutSource
) : SupportRepository(loginSource),
    ILoginRepository<UserInterfaceState<User?>>,
    ILogoutRepository<UserInterfaceState<Boolean>> {

    /**
     * Authenticates a user
     */
    override fun loginUser(query: LoginQuery) =
        UserInterfaceState.create(
            model = loginSource.loginUser(query),
            source = loginSource
        )

    /**
     * Un-authenticates a user
     */
    override fun logoutUser() =
        UserInterfaceState.create(
            model = logoutSource.logoutUser(),
            source = logoutSource
        )

    /**
     * Deals with cancellation of any pending or on going operations that the repository
     * might be working on
     */
    override fun onCleared() {
        super.onCleared()
        logoutSource.cancelAllChildren()
    }
}