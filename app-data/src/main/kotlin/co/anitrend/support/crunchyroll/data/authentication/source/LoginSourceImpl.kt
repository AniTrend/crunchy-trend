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
import androidx.lifecycle.Transformations
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LoginSource
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.mapper.LoginResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.transformer.CrunchyUserTransformer
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.domain.authentication.models.CrunchyLoginQuery
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginSourceImpl(
    private val dao: CrunchyLoginDao,
    private val settings: IAuthenticationSettings,
    private val endpoint: CrunchyAuthenticationEndpoint,
    private val mapper: LoginResponseMapper,
    private val supportConnectivity: SupportConnectivity,
    supportDispatchers: SupportDispatchers
) : LoginSource(supportDispatchers) {

    override val observable =
        object : ISourceObservable<CrunchyLoginQuery, CrunchyUser?> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: CrunchyLoginQuery): LiveData<CrunchyUser?> {
                val login = dao.findLatestByAccountX(parameter.account)

                return Transformations.map(login) {
                    CrunchyUserTransformer.transform(it)
                }
            }
        }

    override fun loginUser(query: CrunchyLoginQuery): LiveData<CrunchyUser?> {
        retry = { loginUser(query) }
        networkState.value = NetworkState.Loading
        val deferred = async {
            endpoint.loginUser(
                account = query.account,
                password = query.password,
                sessionId = settings.sessionId
            )
        }

        launch {
            val controller =
                mapper.controller(supportConnectivity, dispatchers)

            val response = controller(deferred, networkState)
            Timber.tag(moduleTag).i("Logged in userId: ${response?.userId}")
        }

        return observable(query)
    }

    override fun loggedInUser(): LiveData<CrunchyUser?> {
        val userId = settings.authenticatedUserId
        val crunchyLogin = dao.findByUserIdX(userId)
        return Transformations.map(crunchyLogin) {
            CrunchyUserTransformer.transform(it)
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        dao.clearTable()
    }
}