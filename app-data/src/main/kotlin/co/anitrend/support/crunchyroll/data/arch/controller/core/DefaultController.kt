/*
 *    Copyright 2021 AniTrend
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

package co.anitrend.support.crunchyroll.data.arch.controller.core

import co.anitrend.arch.data.common.ISupportResponse
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.contract.ControllerStrategy
import co.anitrend.support.crunchyroll.data.arch.mapper.DefaultMapper
import co.anitrend.support.crunchyroll.data.arch.network.client.DeferrableNetworkClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * General controller that handles complex logic of making requests, capturing errors,
 * notifying state observers and providing input to response mappers
 */
internal class DefaultController<S, out D>(
    private val mapper: DefaultMapper<S, D>,
    private val strategy: ControllerStrategy<D>,
    private val dispatcher: CoroutineDispatcher,
    private val client: DeferrableNetworkClient<S>
) : ISupportResponse<Deferred<Response<S>>, D> {

    /**
     * Response handler for coroutine contexts which need to observe [LoadState]
     *
     * @param resource awaiting execution
     * @param requestCallback for the deferred result
     * @param interceptor allows you to access certain network model fields
     * which are otherwise unaccessable from the domain/entity level
     *
     * @return resource fetched if present
     */
    suspend operator fun invoke(
        resource: Deferred<Response<S>>,
        requestCallback: RequestCallback,
        interceptor: (S) -> S
    ) = strategy(requestCallback) {
        val response = client.fetch(resource)
        val data = interceptor(response)
        val mapped = mapper.onResponseMapFrom(data)
        withContext(dispatcher) {
            mapper.onResponseDatabaseInsert(mapped)
        }
        mapped
    }

    /**
     * Response handler for coroutine contexts which need to observe [LoadState]
     *
     * @param resource awaiting execution
     * @param requestCallback for the deferred result
     *
     * @return resource fetched if present
     */
    override suspend fun invoke(
        resource: Deferred<Response<S>>,
        requestCallback: RequestCallback
    ) = invoke(resource, requestCallback) { it }
}