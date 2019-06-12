package co.anitrend.support.crunchyroll.data.source.auth

import android.os.Bundle
import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.mapper.auth.CrunchyAuthenticationMapper
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import io.wax911.support.data.source.SupportDataSource
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.extension.util.SupportExtKeyStore
import kotlinx.coroutines.*
import org.koin.core.inject
import timber.log.Timber

class CrunchyAuthenticationDataSource(
    parentCoroutineJob: Job? = null,
    private val authEndpoint: CrunchyAuthEndpoint
) : SupportDataSource(parentCoroutineJob) {

    override val databaseHelper by inject<CrunchyDatabase>()
    private val settings by inject<CrunchySettings>()

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
            val session = databaseHelper.crunchySessionCoreDao().findLatest()
            authEndpoint.loginUser(
                account = bundle.getString(AuthRequestType.arg_account),
                password = bundle.getString(AuthRequestType.arg_password),
                sessionId = session?.session_id
            )
        }

        val mapper = CrunchyAuthenticationMapper(
            parentJob = supervisorJob,
            authenticationWithUserDao = databaseHelper.crunchyAuthenticationWithUserDao(),
            authenticatedUserDao = databaseHelper.crunchyUserDao()
        )

        launch {
            mapper.handleResponse(futureResponse, networkState)
        }
    }

    private fun startLogoutFlow() {
        val futureResponse = async {
            val session = databaseHelper.crunchySessionCoreDao().findLatest()
            authEndpoint.logoutUser(
                sessionId = session?.session_id
            )
        }

        val mapper = CrunchyAuthenticationMapper(
            parentJob = supervisorJob,
            authenticationWithUserDao = databaseHelper.crunchyAuthenticationWithUserDao(),
            authenticatedUserDao = databaseHelper.crunchyUserDao()
        )

        launch {
            mapper.handleResponse(futureResponse, networkState)
        }
    }

    /**
     * Clears all the data in a database table which will assure that
     * and refresh the backing storage medium with new network data
     */
    override fun refreshOrInvalidate() {
        launch {
            databaseHelper.crunchyAuthenticationWithUserDao().clearTable()
        }
        super.refreshOrInvalidate()
    }

    val authenticatedUserLiveData = object : ISourceObservable<CrunchyUser?> {

        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param bundle request params, implementation is up to the developer
         */
        override fun observerOnLiveDataWith(bundle: Bundle): LiveData<CrunchyUser?> {
            val authUserDao = databaseHelper.crunchyUserDao()
            return authUserDao.findSingleLiveData(
                userId = settings.authenticatedUserId
            )
        }
    }
}
