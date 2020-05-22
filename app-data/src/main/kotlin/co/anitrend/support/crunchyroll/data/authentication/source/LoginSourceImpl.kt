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

package co.anitrend.support.crunchyroll.data.authentication.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.authentication.mapper.LoginResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LoginSource
import co.anitrend.support.crunchyroll.data.authentication.transformer.CrunchyUserTransformer
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import timber.log.Timber

internal class LoginSourceImpl(
    private val dao: CrunchyLoginDao,
    private val settings: IAuthenticationSettings,
    private val endpoint: CrunchyAuthenticationEndpoint,
    private val mapper: LoginResponseMapper,
    private val supportConnectivity: SupportConnectivity,
    supportDispatchers: SupportDispatchers
) : LoginSource(supportDispatchers) {

    override val observable =
        liveData(coroutineContext) {
            val login = dao.findLatestByAccountX(query.account)

            val loginFlow = login.map {
                CrunchyUserTransformer.transform(it)
            }

            loginFlow.collect {
                emit(it)
            }
        }

    override suspend fun loginUser() {
        val deferred = async {
            endpoint.loginUser(
                account = query.account,
                password = query.password
            )
        }

        val controller =
            mapper.controller(
                dispatchers,
                OnlineControllerPolicy.create(
                    supportConnectivity
                )
            )

        val response = controller(deferred, networkState)
        Timber.tag(moduleTag).i("Logged in userId: ${response?.userId}")
    }

    override fun loggedInUser(): LiveData<CrunchyUser?> {
        val userId = settings.authenticatedUserId
        val userFlow = dao.findByUserIdX(userId).map {
            CrunchyUserTransformer.transform(it)
        }
        return userFlow.asLiveData(context = coroutineContext)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        dao.clearTable()
    }
}