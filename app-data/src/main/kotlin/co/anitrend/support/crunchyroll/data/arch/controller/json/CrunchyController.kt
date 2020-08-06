/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.data.arch.controller.json

import co.anitrend.arch.data.common.ISupportResponse
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.error.RequestError
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.ext.capitalizeWords
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.contract.ControllerStrategy
import co.anitrend.support.crunchyroll.data.arch.extension.fetchBodyWithRetry
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import retrofit2.Response

internal class CrunchyController<S, D> private constructor(
    private val responseMapper: CrunchyMapper<S, D>,
    private val strategy: ControllerStrategy<D>,
    private val dispatchers: SupportDispatchers
) : ISupportResponse<Deferred<Response<CrunchyContainer<S>>>, D> {

    /**
     * Response handler for coroutine contexts, mainly for paging
     *
     * @param resource awaiting execution
     * @param requestCallback request callback
     */
    override suspend fun invoke(
        resource: Deferred<Response<CrunchyContainer<S>>>,
        requestCallback: RequestCallback
    ) = strategy(requestCallback) {
        val response = resource.fetchBodyWithRetry(dispatchers.io)
        if (!response.error) {
            response.data?.let {
                val mapped = responseMapper.onResponseMapFrom(it)
                withContext(dispatchers.io) {
                    responseMapper.onResponseDatabaseInsert(mapped)
                }
                mapped
            }
        } else
            throw RequestError(
                response.code.name.capitalizeWords(),
                response.message,
                null
            )
    }


    companion object {
        fun <S, D> newInstance(
            strategy: ControllerStrategy<D>,
            responseMapper: CrunchyMapper<S, D>,
            supportDispatchers: SupportDispatchers
        ) =
            CrunchyController(
                strategy = strategy,
                responseMapper = responseMapper,
                dispatchers = supportDispatchers
            )
    }
}