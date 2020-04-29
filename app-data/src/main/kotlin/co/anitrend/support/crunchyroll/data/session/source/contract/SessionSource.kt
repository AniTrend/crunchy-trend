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

package co.anitrend.support.crunchyroll.data.session.source.contract

import co.anitrend.arch.data.source.core.SupportCoreDataSource
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.domain.session.entities.Session

internal abstract class SessionSource(
    supportDispatchers: SupportDispatchers
) : SupportCoreDataSource(supportDispatchers) {

    /**
     * Handles the requesting data from a the network source and returns
     * [NetworkState] to the caller after execution
     */
    open operator fun invoke(): Session? {
        networkState.postValue(NetworkState.Loading)
        retry = { invoke() }
        return null
    }
}