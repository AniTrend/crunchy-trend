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

package co.anitrend.support.crunchyroll.data.authentication.mapper

import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.capitalizeWords
import co.anitrend.support.crunchyroll.data.arch.extension.fetchBodyWithRetry
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import retrofit2.Response

class LogoutResponseMapper : CrunchyMapper<Any?, Any?>() {

    /**
     * Response handler for coroutine contexts which need to observe [NetworkState]
     *
     * @param resource awaiting execution
     */
    suspend operator fun invoke(
        resource: Deferred<Response<CrunchyContainer<Any>>>,
        dispatcher: CoroutineDispatcher
    ): NetworkState {
        val result = runCatching {
            val response = resource.fetchBodyWithRetry(dispatcher)
            return if (!response.error) {
                NetworkState.Success
            } else {
                NetworkState.Error(
                    heading = response.code.name.capitalizeWords(),
                    message = response.message
                )
            }
        }

        return result.getOrElse {
            it.printStackTrace()
            NetworkState.Error(
                heading = "Error preventing successful sign out",
                message = it.message
            )
        }
    }

    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     * called in [retrofit2.Callback.onResponse] after assuring that the response was a success
     *
     * @param source the incoming data source type
     * @return Mapped object that will be consumed by [onResponseDatabaseInsert]
     */
    override suspend fun onResponseMapFrom(source: Any?): Any? {
        TODO("Not yet implemented")
    }

    /**
     * Inserts the given object into the implemented room database,
     * called in [retrofit2.Callback.onResponse]
     *
     * @param mappedData mapped object from [onResponseMapFrom] to insert into the database
     */
    override suspend fun onResponseDatabaseInsert(mappedData: Any?) {
        TODO("Not yet implemented")
    }
}