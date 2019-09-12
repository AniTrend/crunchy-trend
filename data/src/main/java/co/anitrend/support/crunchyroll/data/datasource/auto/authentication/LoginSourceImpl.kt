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

package co.anitrend.support.crunchyroll.data.datasource.auto.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthentication
import co.anitrend.support.crunchyroll.data.datasource.auto.authentication.contract.LoginSource
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.mapper.authentication.LoginResponseMapper
import co.anitrend.support.crunchyroll.data.transformer.LoginUserTransformer
import co.anitrend.support.crunchyroll.domain.entities.query.authentication.LoginQuery
import co.anitrend.support.crunchyroll.domain.entities.result.user.User
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginSourceImpl(
    private val dao: CrunchyLoginDao,
    private val endpoint: CrunchyAuthEndpoint,
    private val responseMapper: LoginResponseMapper,
    private val authenticator: CrunchyAuthentication
) : LoginSource() {

    override val observable =
        object : ISourceObservable<LoginQuery, User?> {
            /**
             * Returns the appropriate observable which we will monitor for updates,
             * common implementation may include but not limited to returning
             * data source live data for a database
             *
             * @param parameter to use when executing
             */
            override fun invoke(parameter: LoginQuery): LiveData<User?> {
                val login = dao.findLatestByAccountX(parameter.account)

                return Transformations.map(login) {
                    LoginUserTransformer.transform(it)
                }
            }
        }

    override fun loginUser(query: LoginQuery): LiveData<User?> {
        retry = { loginUser(query) }
        networkState.value = NetworkState.Loading
        val deferred = async {
            val session = authenticator.getCoreSession()
            endpoint.loginUser(
                account = query.account,
                password = query.password,
                sessionId = session?.session_id
            )
        }

        launch {
            responseMapper(deferred, networkState)
        }

        return observable(query)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        dao.clearTable()
    }
}