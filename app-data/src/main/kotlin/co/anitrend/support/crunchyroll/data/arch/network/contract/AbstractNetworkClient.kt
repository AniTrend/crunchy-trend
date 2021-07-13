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

package co.anitrend.support.crunchyroll.data.arch.network.contract

import retrofit2.HttpException
import timber.log.Timber

abstract class AbstractNetworkClient<in Input, out Output> {

    /**
     * @return [Boolean] whether or not the request should be retried based on the [exception] received
     */
    protected abstract fun defaultShouldRetry(exception: Throwable): Boolean

    /**
     * Executes the request
     *
     * @param shouldRetry Conditions to determine when a request should be retried
     * @param defaultDelay Initial delay before retrying
     * @param maxAttempts Max number of attempts to retry
     */
    protected abstract suspend fun Input.execute(
        defaultDelay: Long,
        maxAttempts: Int,
        shouldRetry: (Throwable) -> Boolean
    ): Output

    /**
     *
     */
    protected fun Throwable.getNextDelay(
        attempt: Int,
        maxAttempts: Int,
        defaultDelay: Long,
        shouldRetry: (Throwable) -> Boolean
    ): Long {
        var nextDelay: Long = attempt * attempt * defaultDelay
        Timber.w("Request threw an exception -> $this")

        // The response failed, so lets see if we should retry again
        if (!shouldRetry(this)) {
            Timber.w(this, "Specific request is not allowed to retry on this exception")
        }

        if (attempt == maxAttempts) {
            Timber.w(this, "Cannot retry on exception or maximum retries reached")
        }

        if (this is HttpException) {
            // If we have a HttpException, check whether we have a Retry-After
            // header to decide how long to delay
            val retryAfterHeader = response()?.headers()?.get(RETRY_AFTER_KEY)
            if (retryAfterHeader != null && retryAfterHeader.isNotEmpty()) {
                // Got a Retry-After value, try and parse it to an long
                Timber.i("Rate limit reached")
                try {
                    nextDelay = (retryAfterHeader.toLong() + 10).coerceAtLeast(defaultDelay)
                } catch (nfe: NumberFormatException) {
                    Timber.e(
                        nfe, "Highly unlikely exception was caught on header retry after"
                    )
                }
            }
        }

        Timber.i(
            "Retrying request in $nextDelay ms -> attempt: ${attempt + 1} maxAttempts: $maxAttempts"
        )
        return nextDelay
    }

    companion object {
        internal const val RETRY_AFTER_KEY = "Retry-After"
    }
}