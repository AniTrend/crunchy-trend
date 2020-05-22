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

package co.anitrend.support.crunchyroll.data.authentication.source.contract

import androidx.lifecycle.liveData
import co.anitrend.arch.data.source.coroutine.SupportCoroutineDataSource
import co.anitrend.arch.extension.SupportDispatchers

internal abstract class LogoutSource(
    supportDispatchers: SupportDispatchers
) : SupportCoroutineDataSource(supportDispatchers) {

    protected abstract suspend fun logoutUser(): Boolean

    operator fun invoke() = liveData(context = coroutineContext) {
        retry = { logoutUser() }
        val result = logoutUser()
        emit(result)
    }
}