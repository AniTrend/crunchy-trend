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

import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.authentication.LoginController
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.authentication.mapper.LoginResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LoginSource
import co.anitrend.support.crunchyroll.data.authentication.transformer.CrunchyUserTransformer
import co.anitrend.support.crunchyroll.domain.authentication.models.CrunchyLoginQuery
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber

internal class LoginSourceImpl(
    private val dao: CrunchyLoginDao,
    private val settings: IAuthenticationSettings,
    private val endpoint: CrunchyAuthenticationEndpoint,
    private val controller: LoginController,
    override val dispatcher: ISupportDispatcher
) : LoginSource() {

    override fun observable(query: CrunchyLoginQuery) =
        dao.findLatestByAccountX(query.account)
            .flowOn(dispatcher.io)
            .map { CrunchyUserTransformer.transform(it) }


    override suspend fun loginUser(
        query: CrunchyLoginQuery,
        callback: RequestCallback
    ) {
        val deferred = async {
            endpoint.loginUser(
                account = query.account,
                password = query.password
            )
        }

        val response = controller(deferred, callback)
        Timber.d("Logged in userId: ${response?.userId}")
    }

    override fun loggedInUser(): Flow<CrunchyUser?> {
        val userId = settings.authenticatedUserId.value
        return dao.findByUserIdX(userId)
            .flowOn(dispatcher.io)
            .map { CrunchyUserTransformer.transform(it) }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        withContext(context) {
            dao.clearTable()
        }
    }
}