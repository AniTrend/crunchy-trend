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

package co.anitrend.support.crunchyroll.data.source.auth

import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthenticationHelper
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyLogin
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.mapper.auth.CrunchyLoginMapper
import co.anitrend.support.crunchyroll.data.usecase.auth.CrunchyAuthenticationUseCase
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.source.core.SupportCoreDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.inject
import timber.log.Timber

class CrunchyAuthDataSource(
    private val payload: CrunchyAuthenticationUseCase.Payload,
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val sessionDao: CrunchySessionDao,
    private val authEndpoint: CrunchyAuthEndpoint,
    private val loginDao: CrunchyLoginDao
) : SupportCoreDataSource() {

    private val  authenticationHelper by inject<CrunchyAuthenticationHelper>()

    /**
     * Dispatches work for the paging data source to respective workers or mappers
     * that publish the result to any [androidx.lifecycle.LiveData] observers
     *
     * @see networkState
     */
    override fun invoke() {
        super.invoke()
        when (val requestType = payload.authenticationType) {
            CrunchyAuthenticationUseCase.Payload.RequestType.LOG_IN -> startLoginFlow()
            CrunchyAuthenticationUseCase.Payload.RequestType.LOG_OUT -> startLogoutFlow()
            else -> Timber.tag(moduleTag).w("Unable to identify sessionType: $requestType")
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override fun clearDataSource() {
        launch {
            loginDao.clearTable()
            sessionDao.clearTable()
            sessionCoreDao.clearTable()
        }
    }

    private fun startLoginFlow() {
        val futureResponse = async {
            authEndpoint.loginUser(
                payload = payload.copy(sessionId = sessionCoreDao.findLatest()?.session_id).toMap()
            )
        }

        val mapper = CrunchyLoginMapper(
            parentJob = supervisorJob,
            loginDao = loginDao
        )

        launch {
            mapper.handleResponse(futureResponse, networkState)
        }
    }

    private fun startLogoutFlow() {
        val futureResponse = async {
            authEndpoint.logoutUser(
                sessionId = payload.sessionId
            )
        }

        val mapper = CrunchyLoginMapper(
            parentJob = supervisorJob,
            loginDao = loginDao
        )

        launch {
            val resultState = mapper.handleResponse(futureResponse)
            if (resultState.isLoaded()) {
                authenticationHelper.onInvalidateToken()
                clearDataSource()
            }
            networkState.postValue(resultState)
        }
    }

    val authenticatedUser = object : ISourceObservable<CrunchyLogin?, CrunchyAuthenticationUseCase.Payload?> {
        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param parameter parameters, implementation is up to the developer
         */
        override fun invoke(parameter: CrunchyAuthenticationUseCase.Payload?): LiveData<CrunchyLogin?> {
            invoke()
            return loginDao.findLatestX()
        }
    }
}
