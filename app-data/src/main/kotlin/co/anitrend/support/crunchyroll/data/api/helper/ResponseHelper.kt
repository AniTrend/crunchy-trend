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

package co.anitrend.support.crunchyroll.data.api.helper

import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyResponseStatus
import co.anitrend.support.crunchyroll.data.arch.extension.composeWith
import co.anitrend.support.crunchyroll.data.arch.extension.typeTokenOf
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber

internal class ResponseHelper(private val json: Gson) {

    private fun convertResponse(body: String?) : CrunchyContainer<Any?>? {
        return try {
            Timber.v("Converting response body string to CrunchyContainer")
            json.fromJson(body, typeTokenOf<CrunchyContainer<Any?>?>())
        } catch (e: Exception) {
            Timber.e(Exception(e.message, Throwable(body)))
            CrunchyContainer(CrunchyResponseStatus.bad_request,true,null, body)
        }
    }

    private fun buildBodyUsing(content: String?, mediaType: MediaType?): ResponseBody? {
        Timber.v("Building new response body for mimeType: $mediaType")
        return content?.toResponseBody(mediaType)
    }

    internal fun reconstructResponseUsing(response: Response): Response? {
        val body = response.body?.string()
        val mimeType = response.body?.contentType()
        val currentResponse = convertResponse(body)

        return currentResponse?.composeWith(
            responseBody = buildBodyUsing(body, mimeType),
            response = response
        )
    }
}