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

package co.anitrend.support.crunchyroll.data.arch.mapper

import androidx.paging.PagingRequestHelper
import co.anitrend.support.crunchyroll.data.model.rss.contract.ICrunchyRssChannel
import co.anitrend.support.crunchyroll.data.model.rss.contract.IRssCopyright
import co.anitrend.support.crunchyroll.data.model.rss.core.CrunchyRssMediaContainer
import co.anitrend.support.crunchyroll.data.model.rss.core.CrunchyRssNewsContainer
import io.wax911.support.data.mapper.SupportDataMapper
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.contract.SupportStateContract
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import retrofit2.Response
import timber.log.Timber

@Suppress("UNCHECKED_CAST")
abstract class CrunchyRssMapper<D : IRssCopyright> (
    parentCoroutineJob: Job? = null,
    private val pagingRequestHelper: PagingRequestHelper.Request.Callback? = null
): SupportDataMapper<ICrunchyRssChannel<D>, List<D>>(parentCoroutineJob) {

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
    suspend fun handleResponse(
        deferred: Deferred<Response<CrunchyRssNewsContainer>>
    ): NetworkState {
        try {
            val response = deferred.await()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val mapped = onResponseMapFrom(responseBody.channel as ICrunchyRssChannel<D>)
                    onResponseDatabaseInsert(mapped)
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
        } catch (e: Exception) {
            Timber.tag(moduleTag).e(e)
            return NetworkState.error(e.localizedMessage)
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
    suspend fun handleResponseMedia(
        deferred: Deferred<Response<CrunchyRssMediaContainer>>
    ): NetworkState {
        try {
            val response = deferred.await()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    val mapped = onResponseMapFrom(responseBody.channel as ICrunchyRssChannel<D>)
                    onResponseDatabaseInsert(mapped)
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
        } catch (e: Exception) {
            Timber.tag(moduleTag).e(e)
            return NetworkState.error(e.localizedMessage)
        }
    }
}