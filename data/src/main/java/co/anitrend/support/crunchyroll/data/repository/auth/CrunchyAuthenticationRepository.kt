package co.anitrend.support.crunchyroll.data.repository.auth

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import co.anitrend.support.crunchyroll.data.api.endpoint.CrunchyAuthEndpoint
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyAuthUser
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser
import co.anitrend.support.crunchyroll.data.source.auth.CrunchyAuthenticationDataSource
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.UiModel
import io.wax911.support.data.repository.SupportRepository

class CrunchyAuthenticationRepository(
    private val authEndpoint: CrunchyAuthEndpoint
) : SupportRepository<CrunchyUser?>() {

    /**
     * Handles dispatching of network requests to a background thread
     *
     * @param bundle bundle of parameters for the request
     */
    override fun invokeRequest(bundle: Bundle): UiModel<CrunchyUser?> {
        val dataSource = CrunchyAuthenticationDataSource(
            parentCoroutineJob = supervisorJob,
            authEndpoint = authEndpoint
        )

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            MutableLiveData<NetworkState>()
        }

        return UiModel(
            model = dataSource.authenticatedUserLiveData.observerOnLiveDataWith(bundle),
            networkState = dataSource.networkState,
            refresh = {
                dataSource.refreshOrInvalidate()
                refreshTrigger.value = null
            },
            refreshState = refreshState,
            retry = {
                dataSource.retryFailedRequest()
            }
        )
    }
}