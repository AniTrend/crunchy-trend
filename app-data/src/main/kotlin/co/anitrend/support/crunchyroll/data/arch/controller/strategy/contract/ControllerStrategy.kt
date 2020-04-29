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

package co.anitrend.support.crunchyroll.data.arch.controller.strategy.contract

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingRequestHelper
import co.anitrend.arch.domain.entities.NetworkState

internal abstract class ControllerStrategy<D> {

    protected val moduleTag = javaClass.simpleName

    /**
     * Execute a paging task under an implementation strategy
     *
     * @param block what will be executed
     * @param pagingRequestHelper paging event emitter
     */
    internal abstract suspend operator fun invoke(
        block: suspend () -> Unit,
        pagingRequestHelper: PagingRequestHelper.Request.Callback
    )

    /**
     * Execute a task under an implementation strategy
     *
     * @param block what will be executed
     * @param networkState network state event emitter
     */
    internal abstract suspend operator fun invoke(
        block: suspend () -> D?,
        networkState: MutableLiveData<NetworkState>
    ): D?
}