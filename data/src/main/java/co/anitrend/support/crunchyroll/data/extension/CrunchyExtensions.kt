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

package co.anitrend.support.crunchyroll.data.extension

import co.anitrend.support.crunchyroll.data.arch.ResponseStatus
import co.anitrend.support.crunchyroll.data.arch.ResponseStatusContract
import co.anitrend.support.crunchyroll.data.model.core.CrunchyContainer
import com.google.gson.reflect.TypeToken
import okhttp3.Response
import okhttp3.ResponseBody
import java.lang.reflect.Type
import java.util.*

fun ResponseStatus.toHttpCode() =
    when (this) {
        ResponseStatusContract.BAD_REQUEST -> 400
        ResponseStatusContract.BAD_SESSION -> 401
        ResponseStatusContract.OBJECT_NOT_FOUND -> 404
        ResponseStatusContract.FORBIDDEN -> 403
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



fun getSerivceLocale(): String {
    val locale = Locale.getDefault()
    val language = locale.language
    val country = locale.country
    return "$language$country"
}