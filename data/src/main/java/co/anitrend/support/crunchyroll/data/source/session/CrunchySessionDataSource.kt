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

package co.anitrend.support.crunchyroll.data.source.session

import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchySessionDao
import co.anitrend.support.crunchyroll.data.mapper.session.CrunchySessionCoreMapper
import co.anitrend.support.crunchyroll.data.mapper.session.CrunchySessionMapper
import co.anitrend.support.crunchyroll.data.usecase.session.CrunchySessionUseCase
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.source.coroutine.SupportCoroutineDataSource
import kotlinx.coroutines.*
import timber.log.Timber

class CrunchySessionDataSource(
    private val sessionEndpoint: CrunchySessionEndpoint,
    private val sessionCoreDao: CrunchySessionCoreDao,
    private val authEndpoint: CrunchyAuthEndpoint,
    private val crunchyLoginDao: CrunchyLoginDao,
    private val sessionDao: CrunchySessionDao,
    private val payload: CrunchySessionUseCase.Payload
) : SupportCoroutineDataSource() {

    /**
     * Handles the requesting data from a the network source and return
     * [NetworkState] to the caller after execution.
     *
     * In this context the super.invoke() method will allow a retry action to be set
     */
    override suspend fun invoke(): NetworkState {
        super.invoke()
        return when (val requestType = payload.sessionType) {
            CrunchySessionUseCase.Payload.RequestType.START_CORE_SESSION ->
                startCoreSession()
            CrunchySessionUseCase.Payload.RequestType.START_UNBLOCK_SESSION ->
                startUnblockedSession()
            else -> {
                Timber.tag(moduleTag).w("Unable to identify sessionType: $requestType")
                NetworkState.error("Unable to identify sessionType: $requestType")
            }
        }
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        sessionDao.clearTable()
        sessionCoreDao.clearTable()
    }

    /**
     * Initial session required for core library functionality
     */
    private suspend fun startCoreSession() : NetworkState {
        val futureResponse = async {
            sessionEndpoint.startSession()
        }

        val mapper = CrunchySessionCoreMapper(
            parentJob = supervisorJob,
            sessionCoreDao = sessionCoreDao
        )

        return mapper.handleResponse(futureResponse)

    }

    /**
     * Fallback session when [startUnblockedSession] fails
     */
    private suspend fun startNormalSession() : NetworkState {
        val futureResponse = async {
            authEndpoint.startNormalSession(
                payload = payload.onSessionFallBack().toMap()
            )
        }

        val mapper = CrunchySessionMapper(
            parentJob = supervisorJob,
            sessionDao = sessionDao
        )

        return mapper.handleResponse(futureResponse)
    }

    /**
     * Authenticated session requires user to be signed in, if this sign-in method fails the we should fallback to
     * [startNormalSession]
     */
    private suspend fun startUnblockedSession() : NetworkState {
        val auth = sessionDao.findLatest()?.auth ?: crunchyLoginDao.findLatest()?.auth ?: ""
        val futureResponse = async {
            sessionEndpoint.startUnblockedSession(
                payload = payload.copy(
                    auth = auth
                ).toMap()
            )
        }


        val mapper = CrunchySessionMapper(
            parentJob = supervisorJob,
            sessionDao = sessionDao
        )

        val resultState = mapper.handleResponse(futureResponse)

        if (!resultState.isLoaded()) {
            Timber.tag(moduleTag).w("Unable to create default session, trying normal session")
            return startNormalSession()
        }

        return resultState
    }

    val coreSession = object : ISourceObservable<CrunchySessionCore?, Nothing?> {
        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param parameter parameters, implementation is up to the developer
         */
        override fun invoke(parameter: Nothing?): LiveData<CrunchySessionCore?> {
            return sessionCoreDao.findLatestX()
        }
    }

    val session = object : ISourceObservable<CrunchySession?, Nothing?> {
        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param parameter parameters, implementation is up to the developer
         */
        override fun invoke(parameter: Nothing?): LiveData<CrunchySession?> {
            return sessionDao.findLatestX()
        }
    }
}