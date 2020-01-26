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

import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.CrunchyController
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyResponseStatus
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import com.google.gson.reflect.TypeToken
import okhttp3.Response
import okhttp3.ResponseBody
import java.lang.reflect.Type
import java.util.*

fun CrunchyResponseStatus.toHttpCode() =
    when (this) {
        CrunchyResponseStatus.bad_request -> 400
        CrunchyResponseStatus.bad_session -> 401
        CrunchyResponseStatus.object_not_found -> 404
        CrunchyResponseStatus.bad_auth_params,
        CrunchyResponseStatus.forbidden -> 403
        else -> 200
    }

fun CrunchyContainer<*>.composeWith(response: Response, responseBody: ResponseBody?) =
    Response.Builder()
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
    supportConnectivity: SupportConnectivity,
    supportDispatchers: SupportDispatchers
) = CrunchyController.newInstance(
    responseMapper = this,
    supportConnectivity = supportConnectivity,
    supportDispatchers = supportDispatchers
)

/**
 * Uses system locale to generate a locale string which crunchyroll can use
 */
fun Locale.toCrunchyLocale(): String {
    return "$language$country"
}