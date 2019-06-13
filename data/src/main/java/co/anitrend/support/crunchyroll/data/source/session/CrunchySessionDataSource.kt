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

import android.os.Bundle
import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySession
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.mapper.session.CrunchySessionCoreMapper
import co.anitrend.support.crunchyroll.data.mapper.session.CrunchySessionMapper
import co.anitrend.support.crunchyroll.data.repository.session.SessionRequestType
import io.wax911.support.data.source.SupportDataSource
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.extension.util.SupportExtKeyStore
import kotlinx.coroutines.*
import org.koin.core.inject
import timber.log.Timber

class CrunchySessionDataSource(
    parentCoroutineJob: Job? = null,
    private val sessionEndpoint: CrunchySessionEndpoint,
    private val authEndpoint: CrunchyAuthEndpoint
) : SupportDataSource(parentCoroutineJob) {

    override val databaseHelper by inject<CrunchyDatabase>()

    /**
     * Handles the requesting data from a the network source and informs the
     * network state that it is in the loading state
     *
     * @param bundle request parameters or more
     */
    override fun startRequestForType(bundle: Bundle) {
        super.startRequestForType(bundle)
        when (val requestType = bundle.getString(SupportExtKeyStore.arg_request_type)) {
            SessionRequestType.START_CORE_SESSION -> startCoreSession()
            SessionRequestType.START_UNBLOCK_SESSION -> startUnblockedSession()
            else -> Timber.tag(moduleTag).w("Unable to identify requestType: $requestType")
        }
    }

    /**
     * Initial session required for core library functionality
     */
    private fun startCoreSession() {
        val futureResponse = async {
            sessionEndpoint.startSession()
        }

        val mapper = CrunchySessionCoreMapper(
            parentJob = supervisorJob,
            sessionCoreDao = databaseHelper.crunchySessionCoreDao()
        )

        launch {
            mapper.handleResponse(futureResponse, networkState)
        }
    }

    /**
     * Fallback session when [startUnblockedSession] fails
     */
    private fun startNormalSession() {
        val futureResponse = async {
            val login = databaseHelper.crunchyLoginDao().findLatest()
            val sessionCore = databaseHelper.crunchySessionDao().findLatest()
            authEndpoint.startNormalSession(
                device_id = sessionCore?.device_id,
                auth = login?.auth
            )
        }

        val mapper = CrunchySessionMapper(
            parentJob = supervisorJob,
            userDao = databaseHelper.crunchyUserDao(),
            sessionDao = databaseHelper.crunchySessionDao()
        )

        launch {
            mapper.handleResponse(futureResponse, networkState)
        }
    }

    /**
     * Authenticated session requires user to be signed in, if this sign-in method fails the we should fallback to
     * [startNormalSession]
     */
    private fun startUnblockedSession() {
        val futureResponse = async {
            val login = databaseHelper.crunchyLoginDao().findLatest()
            sessionEndpoint.startUnblockedSession(
                auth = login?.auth,
                userId = login?.user?.user_id
            )
        }


        val mapper = CrunchySessionMapper(
            parentJob = supervisorJob,
            userDao= databaseHelper.crunchyUserDao(),
            sessionDao = databaseHelper.crunchySessionDao()
        )

        launch {
            val resultState = mapper.handleResponse(futureResponse)
            withContext(Dispatchers.Main) {
                if (!resultState.isLoaded())
                    startNormalSession()
                else
                    networkState.postValue(resultState)
            }
        }
    }

    val coreSession = object : ISourceObservable<CrunchySessionCore?> {

        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param bundle request params, implementation is up to the developer
         */
        override fun observerOnLiveDataWith(bundle: Bundle): LiveData<CrunchySessionCore?> {
            val coreDao = databaseHelper.crunchySessionCoreDao()
            return coreDao.findLatestX()
        }
    }

    val session = object : ISourceObservable<CrunchySession?> {

        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param bundle request params, implementation is up to the developer
         */
        override fun observerOnLiveDataWith(bundle: Bundle): LiveData<CrunchySession?> {
            val sessionDao = databaseHelper.crunchySessionDao()
            return sessionDao.findLatestX()
        }
    }

    /**
     * Clears all the data in a database table which will assure that
     * and refresh the backing storage medium with new network data
     */
    override fun refreshOrInvalidate() {
        launch {
            databaseHelper.crunchySessionDao().clearTable()
            databaseHelper.crunchySessionCoreDao().clearTable()
        }
        super.refreshOrInvalidate()
    }
}