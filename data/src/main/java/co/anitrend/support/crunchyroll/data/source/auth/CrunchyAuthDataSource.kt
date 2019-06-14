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

import android.os.Bundle
import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.auth.CrunchyAuthenticationHelper
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyLogin
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.dao.query.CrunchyUserDao
import co.anitrend.support.crunchyroll.data.mapper.auth.CrunchyLoginMapper
import co.anitrend.support.crunchyroll.data.repository.auth.AuthRequestType
import io.wax911.support.data.source.SupportDataSource
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.extension.util.SupportExtKeyStore
import kotlinx.coroutines.*
import org.koin.core.inject
import timber.log.Timber

class CrunchyAuthDataSource(
    parentCoroutineJob: Job? = null,
    private val authEndpoint: CrunchyAuthEndpoint,
    private val loginDao: CrunchyLoginDao,
    private val userDao: CrunchyUserDao,
    private val sessionCoreDao: CrunchySessionCoreDao
) : SupportDataSource(parentCoroutineJob) {

    private val  authenticationHelper by inject<CrunchyAuthenticationHelper>()

    /**
     * Handles the requesting data from a the network source and informs the
     * network state that it is in the loading state
     *
     * @param bundle request parameters or more
     */
    override fun startRequestForType(bundle: Bundle) {
        super.startRequestForType(bundle)

        when (val requestType = bundle.getString(SupportExtKeyStore.arg_request_type)) {
            AuthRequestType.LOG_IN -> startLoginFlow(bundle)
            AuthRequestType.LOG_OUT -> startLogoutFlow()
            else -> Timber.tag(moduleTag).w("Unable to identify requestType: $requestType")
        }
    }

    private fun startLoginFlow(bundle: Bundle) {
        val futureResponse = async {
            val session = sessionCoreDao.findLatest()
            authEndpoint.loginUser(
                account = bundle.getString(AuthRequestType.arg_account),
                password = bundle.getString(AuthRequestType.arg_password),
                sessionId = session?.session_id
            )
        }

        val mapper = CrunchyLoginMapper(
            parentJob = supervisorJob,
            loginDao = loginDao,
            userDao = userDao
        )

        launch {
            mapper.handleResponse(futureResponse, networkState)
        }
    }

    private fun startLogoutFlow() {
        val futureResponse = async {
            val session = sessionCoreDao.findLatest()
            authEndpoint.logoutUser(
                sessionId = session?.session_id
            )
        }

        val mapper = CrunchyLoginMapper(
            parentJob = supervisorJob,
            loginDao = loginDao,
            userDao = userDao
        )

        launch {
            val resultState = mapper.handleResponse(futureResponse)
            if (resultState.isLoaded())
                authenticationHelper.onInvalidToken(
                    userDao,
                    loginDao,
                    sessionCoreDao
                )
            networkState.postValue(resultState)
        }
    }

    /**
     * Clears all the data in a database table which will assure that
     * and refresh the backing storage medium with new network data
     */
    override fun refreshOrInvalidate() {
        launch {
            loginDao.clearTable()
        }
        super.refreshOrInvalidate()
    }

    val authenticatedUserLiveData = object : ISourceObservable<CrunchyLogin?> {

        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param bundle request params, implementation is up to the developer
         */
        override fun observerOnLiveDataWith(bundle: Bundle): LiveData<CrunchyLogin?> {
            val loginDao = loginDao
            return loginDao.findLatestX()
        }
    }
}
