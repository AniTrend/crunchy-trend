package co.anitrend.support.crunchyroll.data.arch.mapper

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingRequestHelper
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import io.wax911.support.data.mapper.SupportDataMapper
import io.wax911.support.data.mapper.contract.IMapperHelper
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.contract.SupportStateContract
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import retrofit2.Response
import timber.log.Timber

abstract class CrunchyMapper<S, D> (
    parentCoroutineJob: Job? = null,
    private val pagingRequestHelper: PagingRequestHelper.Request.Callback? = null
): SupportDataMapper<S, D>(parentCoroutineJob), IMapperHelper<Response<CrunchyContainer<S>>> {


    /**
     * Response handler for coroutine contexts which need to observe
     * the live data of [NetworkState]
     *
     * Unless when if using [androidx.paging.PagingRequestHelper.Request.Callback]
     * then you can ignore the return type
     *
     * @param deferred an deferred result awaiting execution
     * @return network state of the deferred result
     */
    override suspend fun handleResponse(deferred: Deferred<Response<CrunchyContainer<S>>>): NetworkState {
        val response = deferred.await()
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                if (!responseBody.error) {
                    if (responseBody.data != null) {
                        val mapped = onResponseMapFrom(responseBody.data)
                        onResponseDatabaseInsert(mapped)
                    } else
                        Timber.tag(moduleTag).i("Response was a success but response body was empty.")
                } else {
                    pagingRequestHelper?.recordFailure(Throwable(responseBody.message))
                    return NetworkState(
                        status = SupportStateContract.ERROR,
                        message = responseBody.message,
                        code = response.code()
                    )
                }
            }
            pagingRequestHelper?.recordSuccess()
            return NetworkState.LOADED
        } else {
            pagingRequestHelper?.recordFailure(Throwable(response.message()))
            return NetworkState(
                status = SupportStateContract.ERROR,
                message = response.message(),
                code = response.code()
            )
        }
    }
    /**
     * Response handler for coroutine contexts which need to observe
     * the live data of [NetworkState]
     *
     * Unless when if using [androidx.paging.PagingRequestHelper.Request.Callback]
     * then you can ignore the return type
     *
     * @param deferred an deferred result awaiting execution
     * @return network state of the deferred result
     */
    suspend fun handleResponse(deferred: Deferred<Response<CrunchyContainer<S>>>, networkState: MutableLiveData<NetworkState>) {
        val resultState = handleResponse(deferred)
        networkState.postValue(resultState)
    }
}