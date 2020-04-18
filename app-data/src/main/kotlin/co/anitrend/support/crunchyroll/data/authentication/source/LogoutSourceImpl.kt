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
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.domain.extensions.isSuccess
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.datasource.remote.CrunchyAuthenticationEndpoint
import co.anitrend.support.crunchyroll.data.authentication.mapper.LogoutResponseMapper
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.authentication.source.contract.LogoutSource
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.session.repository.SessionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LogoutSourceImpl(
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val mapper: LogoutResponseMapper,
    private val sessionDao: CrunchySessionDao,
    private val endpoint: CrunchyAuthenticationEndpoint,
    private val settings: IAuthenticationSettings,
    private val dao: CrunchyLoginDao,
    private val sessionRepository: SessionRepository,
    supportDispatchers: SupportDispatchers
) : LogoutSource(supportDispatchers) {

    override fun logoutUser(): LiveData<Boolean> {
        retry = { logoutUser() }
        networkState.postValue(NetworkState.Loading)
        val deferred = async {
            if (settings.sessionId == null) {
                val unblocked = sessionRepository.getUnblockedSession()
                if (unblocked == null)
                    sessionRepository.getCoreSession()
            }
            endpoint.logoutUser(
                sessionId = settings.sessionId
            )
        }

        launch {
            val state = mapper(deferred, dispatchers.io)
            if (state.isSuccess()) {
                clearDataSource()
                with(settings) {
                    authenticatedUserId = IAuthenticationSettings.INVALID_USER_ID
                    hasAccessToPremium = false
                    isAuthenticated = false
                    sessionId = null
                }
            }
            networkState.postValue(state)
            observable.postValue(state.isSuccess())
        }

        return observable
    }


    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        sessionCoreDao.clearTable()
        sessionDao.clearTable()
        dao.clearTable()
    }
}