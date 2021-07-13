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
import co.anitrend.support.crunchyroll.data.authentication.LogoutController
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LogoutSource
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

internal class LogoutSourceImpl(
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val sessionDao: CrunchySessionDao,
    private val endpoint: CrunchyAuthenticationEndpoint,
    private val settings: IAuthenticationSettings,
    private val controller: LogoutController,
    private val dao: CrunchyLoginDao,
    override val dispatcher: ISupportDispatcher
) : LogoutSource() {

    override suspend fun logoutUser(callback: RequestCallback): Boolean {
        val deferred = async {
            endpoint.logoutUser()
        }

        val result = controller(deferred, callback)

        if (result != null) {
            clearDataSource(dispatcher.io)
            return true
        }

        return false
    }


    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource(context: CoroutineDispatcher) {
        withContext(context) {
            settings.authenticatedUserId.value = IAuthenticationSettings.INVALID_USER_ID
            settings.hasAccessToPremium.value = false
            settings.isAuthenticated.value = false
            settings.sessionId.value = null

            sessionCoreDao.clearTable()
            sessionDao.clearTable()
            dao.clearTable()
        }
    }
}