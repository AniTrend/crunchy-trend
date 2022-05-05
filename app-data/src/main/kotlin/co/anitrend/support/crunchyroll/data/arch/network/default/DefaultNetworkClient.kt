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

package co.anitrend.support.crunchyroll.data.arch.network.default

import co.anitrend.arch.domain.entities.RequestError
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyResponseStatus
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.arch.network.client.DeferrableNetworkClient
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber

/**
 * Default network client
 */
internal class DefaultNetworkClient<R>(
    override val dispatcher: CoroutineDispatcher
) : DeferrableNetworkClient<R>() {

    private fun Response<R>.responseError(): R {
        runCatching {
            val errorBodyString = errorBody()?.string()
            throw RequestError(
                topic = "Failed to complete request",
                description = errorBodyString
            )
        }.onFailure { exception ->
            Timber.w(exception)
        }
        throw HttpException(this)
    }

    /**
     * @return [Response.body] of the response
     *
     * @throws HttpException When the request was not successful
     */
    override fun Response<R>.bodyOrThrow(): R {
        if (!isSuccessful)
            return responseError()

        val container = requireNotNull(body()) {
            "Response<T>.bodyOrThrow() -> response body was null"
        }

        if (container is CrunchyContainer<*>) {
            return when (container.code) {
                CrunchyResponseStatus.bad_auth_params -> throw RequestError()
                CrunchyResponseStatus.bad_request -> throw RequestError()
                CrunchyResponseStatus.bad_session -> throw RequestError()
                CrunchyResponseStatus.object_not_found -> throw RequestError()
                CrunchyResponseStatus.forbidden -> throw RequestError()
                CrunchyResponseStatus.error -> throw RequestError()
                CrunchyResponseStatus.ok -> container
            }
        }

        return container
    }
}