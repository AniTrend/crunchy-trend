package co.anitrend.support.crunchyroll.data.source.session

import android.os.Bundle
import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.CrunchySessionEndpoint
import co.anitrend.support.crunchyroll.data.auth.model.CrunchySessionCore
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.entity.session.CrunchySessionWithAuthenticatedUser
import co.anitrend.support.crunchyroll.data.mapper.session.CrunchySessionCoreMapper
import co.anitrend.support.crunchyroll.data.mapper.session.CrunchySessionMapper
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
            val authUser = databaseHelper.crunchyAuthenticationWithUserDao().findLatest()
            val sessionCore = databaseHelper.crunchySessionDao().findLatest()
            authEndpoint.startNormalSession(
                device_id = sessionCore?.device_id,
                auth = authUser?.auth
            )
        }

        val mapper = CrunchySessionMapper(
            parentJob = supervisorJob,
            authenticationWithUserDao = databaseHelper.crunchyAuthenticationWithUserDao(),
            sessionWithAuthenticatedUserDao = databaseHelper.crunchySessionDao()
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
            val authUser = databaseHelper.crunchyAuthenticationWithUserDao().findLatest()
            sessionEndpoint.startUnblockedSession(
                auth = authUser?.auth,
                userId = authUser?.userId
            )
        }


        val mapper = CrunchySessionMapper(
            parentJob = supervisorJob,
            authenticationWithUserDao = databaseHelper.crunchyAuthenticationWithUserDao(),
            sessionWithAuthenticatedUserDao = databaseHelper.crunchySessionDao()
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
            return coreDao.findLatestLiveData()
        }
    }

    val session = object : ISourceObservable<CrunchySessionWithAuthenticatedUser?> {

        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param bundle request params, implementation is up to the developer
         */
        override fun observerOnLiveDataWith(bundle: Bundle): LiveData<CrunchySessionWithAuthenticatedUser?> {
            val sessionDao = databaseHelper.crunchySessionDao()
            return sessionDao.findLatestLiveData()
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