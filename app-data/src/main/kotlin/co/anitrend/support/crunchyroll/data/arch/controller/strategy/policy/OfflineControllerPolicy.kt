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

package co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy

import androidx.lifecycle.MutableLiveData
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.error.RequestError
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.contract.ControllerStrategy
import timber.log.Timber

/**
 * Does not run any connectivity check before prior to execution, this is useful for sources
 * that may have caching on the network level through interception or cache-control from
 * the origin server.
 */
internal class OfflineControllerPolicy<D> private constructor() : ControllerStrategy<D>() {

    /**
     * Execute a paging task under an implementation strategy
     *
     * @param requestCallback event emitter
     * @param block what will be executed
     */
    override suspend fun invoke(
        requestCallback: RequestCallback,
        block: suspend () -> D?
    ) = runCatching {
        val result = block()
        requestCallback.recordSuccess()
        result
    }.onFailure { e->
        Timber.tag(moduleTag).e(e)
        if (e is RequestError)
            requestCallback.recordFailure(e)
        else
            requestCallback.recordFailure(
                RequestError("Unexpected error" , e.message, e.cause)
            )
    }.getOrNull()

    companion object {
        fun <T> create() =
            OfflineControllerPolicy<T>()
    }
}