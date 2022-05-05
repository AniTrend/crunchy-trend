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

package co.anitrend.support.crunchyroll.data.arch.network.client

import co.anitrend.arch.domain.entities.RequestError
import co.anitrend.support.crunchyroll.data.arch.network.contract.AbstractNetworkClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * A network client that manages how requests should be run and when to retry
 *
 * @property dispatcher A [CoroutineDispatcher] that should be used for running requests
 */
abstract class DeferrableNetworkClient<T> : AbstractNetworkClient<Deferred<Response<T>>, Response<T>>() {

    protected abstract val dispatcher: CoroutineDispatcher

    /**
     * @return [Response.body] of the response
     *
     * @throws HttpException When the request was not successful
     */
    @Throws(HttpException::class)
    protected open fun Response<T>.bodyOrThrow(): T {
        if (!isSuccessful) throw HttpException(this)
        return requireNotNull(body()) {
            "Response<T>.bodyOrThrow() -> response body was null"
        }
    }

    /**
     * @return [Boolean] whether or not the request should be retried based on the [exception] received
     */
    override fun defaultShouldRetry(exception: Throwable) = when (exception) {
        is HttpException -> exception.code() == 429
        is SocketTimeoutException,
        is IOException -> true
        else -> false
    }

    /**
     * Executes the request
     *
     * @param shouldRetry Conditions to determine when a request should be retried
     * @param defaultDelay Initial delay before retrying
     * @param maxAttempts Max number of attempts to retry
     */
    override suspend fun Deferred<Response<T>>.execute(
        defaultDelay: Long,
        maxAttempts: Int,
        shouldRetry: (Throwable) -> Boolean
    ): Response<T> {
        var lastKnownException: Throwable? = null

        repeat(maxAttempts) { attempt ->
            runCatching {
                withContext(dispatcher) { await() }
            }.onSuccess { response ->
                return response
            }.onFailure { exception ->
                if (lastKnownException != exception)
                    lastKnownException = exception
                delay(
                    exception.getNextDelay(
                        attempt,
                        maxAttempts,
                        defaultDelay,
                        shouldRetry
                    )
                )
            }
        }

        throw lastKnownException ?: RequestError(
            "Unable to recover from unknown error",
            "Maximum retry attempts exhausted without success"
        )
    }

    /**
     * Automatically runs the suspendable operation and returns the body
     *
     * @param deferredRequest The request which needs to be executed
     * @param shouldRetry Conditions to determine when a request should be retried
     * @param firstDelay Initial delay before retrying
     * @param maxAttempts Max number of attempts to retry
     *
     * @throws HttpException When the [maxAttempts] have been exhausted, or unhandled exception
     */
    @Throws(HttpException::class)
    open suspend fun fetch(
        deferredRequest: Deferred<Response<T>>,
        firstDelay: Long = 500,
        maxAttempts: Int = 3,
        shouldRetry: (Throwable) -> Boolean = ::defaultShouldRetry,
    ) = deferredRequest.execute(
        firstDelay,
        maxAttempts,
        shouldRetry
    ).bodyOrThrow()
}