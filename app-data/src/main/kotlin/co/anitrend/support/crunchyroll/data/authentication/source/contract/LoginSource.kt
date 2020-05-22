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

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.source.coroutine.SupportCoroutineDataSource
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.domain.authentication.models.CrunchyLoginQuery
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

internal abstract class LoginSource(
    supportDispatchers: SupportDispatchers
) : SupportCoroutineDataSource(supportDispatchers) {

    protected lateinit var query: CrunchyLoginQuery
        private set

    protected abstract val observable: LiveData<CrunchyUser?>

    protected abstract suspend fun loginUser()
    abstract fun loggedInUser(): LiveData<CrunchyUser?>

    operator fun invoke(param: CrunchyLoginQuery) =
        liveData(coroutineContext) {
            query = param
            retry = { loginUser() }
            loginUser()
            emitSource(
                observable
            )
        }
}