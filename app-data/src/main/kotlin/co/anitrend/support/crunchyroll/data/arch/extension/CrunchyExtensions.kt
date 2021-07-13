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

package co.anitrend.support.crunchyroll.data.arch.extension

import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyResponseStatus
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import okhttp3.Response
import java.lang.reflect.Type


internal fun Response?.isCached(): Boolean {
    return if (this != null) cacheResponse != null else false
}

internal fun CrunchyResponseStatus.toHttpCode() =
    when (this) {
        CrunchyResponseStatus.ok -> 200
        CrunchyResponseStatus.bad_request -> 400
        CrunchyResponseStatus.bad_session,
        CrunchyResponseStatus.bad_auth_params -> 401
        CrunchyResponseStatus.object_not_found -> 404
        CrunchyResponseStatus.forbidden -> 403
        CrunchyResponseStatus.error -> 429
    }

internal fun CrunchyContainer<*>.composeWith(response: Response, responseBody: ResponseBody?) =
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

internal inline fun <reified T> typeTokenOf(): Type =
    object : TypeToken<T>() {}.type