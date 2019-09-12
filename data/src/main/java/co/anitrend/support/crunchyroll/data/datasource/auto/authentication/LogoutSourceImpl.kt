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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.domain.entities.isSuccess
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.datasource.auto.authentication.contract.LogoutSource
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.datasource.local.api.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.log

class LogoutSourceImpl(
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val sessionDao: CrunchySessionDao,
    private val endpoint: CrunchyAuthEndpoint,
    private val dao: CrunchyLoginDao,
    private val settings: CrunchySettings
) : LogoutSource() {

    override fun logoutUser(): LiveData<Boolean> {
        retry = { logoutUser() }
        networkState.value = NetworkState.Loading
        val deferred = async {
            val session = sessionCoreDao.findLatest()
            endpoint.logoutUser(
                sessionId = session?.session_id
            )
        }

        Transformations.map(networkState) {
            if (it.isSuccess()) {
                launch { clearDataSource() }
                with (settings) {
                    authenticatedUserId = CrunchySettings.INVALID_USER_ID
                    isAuthenticated = false
                }
            }
            observable.postValue(it.isSuccess())
        }

        launch {
            CrunchyMapper(deferred, networkState)
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