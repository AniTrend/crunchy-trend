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
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.domain.extensions.isSuccess
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.authentication.mapper.LogoutResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LogoutSource
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

internal class LogoutSourceImpl(
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val mapper: LogoutResponseMapper,
    private val sessionDao: CrunchySessionDao,
    private val endpoint: CrunchyAuthenticationEndpoint,
    private val settings: IAuthenticationSettings,
    private val supportConnectivity: SupportConnectivity,
    private val dao: CrunchyLoginDao,
    supportDispatchers: SupportDispatchers
) : LogoutSource(supportDispatchers) {

    override suspend fun logoutUser(callback: RequestCallback): Boolean {
        val deferred = async {
            endpoint.logoutUser()
        }

        val controller = mapper.controller(
            dispatchers,
            OnlineControllerPolicy.create(
                supportConnectivity
            )
        )

        controller(deferred, callback)
        val result = networkState.firstOrNull()
        if (result == NetworkState.Success)
            clearDataSource(dispatchers.io)

        return result?.isSuccess() ?: false
    }


    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        withContext(context) {
            settings.authenticatedUserId = IAuthenticationSettings.INVALID_USER_ID
            settings.hasAccessToPremium = false
            settings.isAuthenticated = false
            settings.sessionId = null

            sessionCoreDao.clearTable()
            sessionDao.clearTable()
            dao.clearTable()
        }
    }
}