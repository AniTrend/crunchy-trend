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

package co.anitrend.support.crunchyroll.data.api.endpoint.contract

import co.anitrend.support.crunchyroll.data.BuildConfig
import co.anitrend.support.crunchyroll.data.api.converter.CrunchyConverterFactory
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyInterceptor
import io.wax911.support.data.factory.SupportEndpointFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass


/**
 * Generates retrofit service classes
 *
 * @param url The url to use when create a service endpoint
 * @param endpoint The interface class method representing your request to use
 */
open class CrunchyEndpointFactory<S: Any>(
    url: String,
    endpoint: KClass<S>,
    private val injectInterceptor: Boolean = true
) : SupportEndpointFactory<S>(url, endpoint), KoinComponent {

    private val clientInterceptor by inject<CrunchyInterceptor>()

    override val retrofit: Retrofit by lazy {
        val httpClient = OkHttpClient.Builder()
            .apply {
                if (injectInterceptor)
                    addInterceptor(clientInterceptor)
                readTimeout(35, TimeUnit.SECONDS)
                connectTimeout(35, TimeUnit.SECONDS)
                retryOnConnectionFailure(true)
                when {
                    BuildConfig.DEBUG -> {
                        val httpLoggingInterceptor = HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.HEADERS)
                        addInterceptor(httpLoggingInterceptor)
                    }
                }
            }.build()

        Retrofit.Builder().client(httpClient).apply {
            addConverterFactory(
                CrunchyConverterFactory.create()
            ).baseUrl(url)
        }.build()
    }
}