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

package co.anitrend.support.crunchyroll.data.api.interceptor

import co.anitrend.arch.extension.LAZY_MODE_SYNCHRONIZED
import co.anitrend.support.crunchyroll.data.api.converter.CrunchyConverterFactory
import co.anitrend.support.crunchyroll.data.api.helper.ResponseHelper
import okhttp3.Interceptor
import okhttp3.Response

internal class CrunchySessionInterceptor : Interceptor {

    private val responseHelper by lazy(LAZY_MODE_SYNCHRONIZED) {
        ResponseHelper(
            json = CrunchyConverterFactory.GSON_BUILDER.create()
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val response = chain.proceed(original)
        return responseHelper.reconstructResponseUsing(response) ?: response
    }
}