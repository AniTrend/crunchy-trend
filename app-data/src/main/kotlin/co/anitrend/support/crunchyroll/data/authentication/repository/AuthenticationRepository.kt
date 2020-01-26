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

package co.anitrend.support.crunchyroll.data.authentication.repository

import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.arch.data.repository.SupportRepository
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LoginSource
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LogoutSource
import co.anitrend.support.crunchyroll.domain.authentication.models.CrunchyLoginQuery
import co.anitrend.support.crunchyroll.domain.authentication.repositories.ILoginRepository
import co.anitrend.support.crunchyroll.domain.authentication.repositories.ILogoutRepository
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser

class AuthenticationRepository(
    private val loginSource: LoginSource,
    private val logoutSource: LogoutSource
) : SupportRepository(loginSource),
    ILoginRepository<UserInterfaceState<CrunchyUser?>>,
    ILogoutRepository<UserInterfaceState<Boolean>> {


    override fun loggedInUser() =
        UserInterfaceState.create(
            model = loginSource.loggedInUser(),
            source = loginSource
        )

    /**
     * Authenticates a user
     */
    override fun loginUser(query: CrunchyLoginQuery) =
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