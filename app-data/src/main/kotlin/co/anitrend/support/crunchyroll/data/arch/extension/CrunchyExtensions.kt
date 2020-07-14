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

package co.anitrend.support.crunchyroll.data.arch.extension

import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.api.provider.EndpointProvider
import co.anitrend.support.crunchyroll.data.arch.controller.json.CrunchyController
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.contract.ControllerStrategy
import co.anitrend.support.crunchyroll.data.arch.controller.xml.CrunchyXmlController
import co.anitrend.support.crunchyroll.data.arch.database.common.ICrunchyDatabase
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyResponseStatus
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyRssMapper
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.rss.contract.IRssCopyright
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.koin.core.scope.Scope
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type
import java.util.*
import okhttp3.Response as OkHttpResponse

fun OkHttpResponse?.isCached(): Boolean {
    return if (this != null) cacheResponse != null else false
}

fun CrunchyResponseStatus.toHttpCode() =
    when (this) {
        CrunchyResponseStatus.ok -> 200
        CrunchyResponseStatus.bad_request -> 400
        CrunchyResponseStatus.bad_session,
        CrunchyResponseStatus.bad_auth_params -> 401
        CrunchyResponseStatus.object_not_found -> 404
        CrunchyResponseStatus.forbidden -> 403
        CrunchyResponseStatus.error -> 429
    }

fun CrunchyContainer<*>.composeWith(response: OkHttpResponse, responseBody: ResponseBody?) =
    OkHttpResponse.Builder()
        .code(code.toHttpCode())
        .body(responseBody)
        .message(message ?: response.message)
        .headers(response.headers)
        .cacheResponse(response.cacheResponse)
        .priorResponse(response.priorResponse)
        .request(response.request)
        .protocol(response.protocol)
        .sentRequestAtMillis(response.sentRequestAtMillis)
        .receivedResponseAtMillis(response.receivedResponseAtMillis)
        .build()

inline fun <reified T> typeTokenOf(): Type =
    object : TypeToken<T>() {}.type

/**
 * Extension to help us create a controller from a a mapper instance
 */
internal fun <S, D> CrunchyMapper<S, D>.controller(
    supportDispatchers: SupportDispatchers,
    strategy: ControllerStrategy<D>
) = CrunchyController.newInstance(
    strategy = strategy,
    responseMapper = this,
    supportDispatchers = supportDispatchers
)

internal fun <S: IRssCopyright, D> CrunchyRssMapper<S, D>.controller(
    supportDispatchers: SupportDispatchers,
    strategy: ControllerStrategy<D>
) = CrunchyXmlController.newInstance(
    strategy = strategy,
    responseMapper = this,
    supportDispatchers = supportDispatchers
)

/**
 * Uses system locale to generate a locale string which crunchyroll can use
 */
fun Locale.toCrunchyLocale(): String {
    return "$language$country"
}

internal fun Scope.db() = get<ICrunchyDatabase>()

internal inline fun <reified T> Scope.api(endpointType: EndpointType): T =
    EndpointProvider.provideRetrofit(endpointType, this).create(T::class.java)

private fun <T> Response<T>.bodyOrThrow(): T {
    if (!isSuccessful) throw HttpException(this)
    return body()!!
}

private fun defaultShouldRetry(exception: Exception) = when (exception) {
    is HttpException -> exception.code() == 429 || exception.code() == 401
    is IOException -> true
    else -> false
}

private suspend inline fun <T> Deferred<Response<T>>.executeWithRetry(
    dispatcher: CoroutineDispatcher,
    defaultDelay: Long = 100,
    maxAttempts: Int = 3,
    shouldRetry: (Exception) -> Boolean = ::defaultShouldRetry
): Response<T> {
    repeat(maxAttempts) { attempt ->
        var nextDelay = attempt * attempt * defaultDelay
        try {
            return withContext(dispatcher) { await() }
        } catch (e: Exception) {
            // The response failed, so lets see if we should retry again
            if (attempt == (maxAttempts - 1) || !shouldRetry(e)) {
                throw e
            }

            if (e is HttpException) {
                // If we have a HttpException, check whether we have a Retry-After
                // header to decide how long to delay
                val retryAfterHeader = e.response()?.headers()?.get("Retry-After")
                if (retryAfterHeader != null && retryAfterHeader.isNotEmpty()) {
                    // Got a Retry-After value, try and parse it to an long
                    try {
                        nextDelay = (retryAfterHeader.toLong() + 10).coerceAtLeast(defaultDelay)
                    } catch (nfe: NumberFormatException) {
                        // Probably won't happen, ignore the value and use the generated default above
                    }
                }
            }
        }

        delay(nextDelay)
    }

    // We should never hit here
    throw IllegalStateException("Unknown exception from executeWithRetry")
}

internal suspend inline fun <T> Deferred<Response<T>>.fetchBodyWithRetry(
    dispatcher: CoroutineDispatcher,
    firstDelay: Long = 100,
    maxAttempts: Int = 3,
    shouldRetry: (Exception) -> Boolean = ::defaultShouldRetry
) = executeWithRetry(dispatcher, firstDelay, maxAttempts, shouldRetry).bodyOrThrow()