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

package co.anitrend.support.crunchyroll.data.api.provider

import androidx.collection.LruCache
import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyCacheInterceptor
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyRequestInterceptor
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyResponseInterceptor
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchySessionInterceptor
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import retrofit2.Retrofit
import timber.log.Timber

internal object EndpointProvider {

    private val moduleTag = javaClass.simpleName
    private val retrofitCache = LruCache<EndpointType, Retrofit>(3)

    private fun provideOkHttpClient(endpointType: EndpointType, scope: Scope) : OkHttpClient {
        val builder = scope.get<OkHttpClient.Builder> {
            parametersOf(
                when (endpointType) {
                    EndpointType.XML,
                    EndpointType.SLUG-> HttpLoggingInterceptor.Level.HEADERS
                    else -> HttpLoggingInterceptor.Level.BODY
                }
            )
        }

        when (endpointType) {
            EndpointType.JSON -> {
                Timber.tag(moduleTag).d("""
                    Adding request and response interceptors for request: ${endpointType.name}
                    """.trimIndent()
                )
                val dispatcher = Dispatcher().apply {
                    // Setting the dispatcher to only run one request at a time
                    maxRequests = 1
                }
                builder
                    .dispatcher(dispatcher)
                    .addInterceptor(
                        CrunchyRequestInterceptor(
                            authentication = scope.get(),
                            connectivity = scope.get(),
                            dispatcher = scope.get()
                        )
                    )
                    .addInterceptor(
                        CrunchyResponseInterceptor(
                            authentication = scope.get(),
                            connectivity = scope.get(),
                            dispatchers = scope.get()
                        )
                    )
            }
            EndpointType.XML -> {
                Timber.tag(moduleTag).d(
                    "Adding dedicated cache interceptor for request: ${endpointType.name}"
                )
                builder.addInterceptor(
                    CrunchyCacheInterceptor(
                        connectivity = scope.get()
                    )
                )
            }
            EndpointType.SESSION_PROXY,
            EndpointType.SESSION_CORE,
            EndpointType.SESSION_JSON -> {
                Timber.tag(moduleTag).d(
                    "Adding response converter interceptor for: ${endpointType.name}"
                )
                builder.addInterceptor(
                    CrunchySessionInterceptor()
                )
            }
            else -> {
                Timber.tag(moduleTag).d("No interceptors to add for: ${endpointType.name}")
            }
        }

        return builder.build()
    }

    private fun createRetrofit(endpointType: EndpointType, scope: Scope) : Retrofit {
        return scope.get<Retrofit.Builder>()
            .client(
                provideOkHttpClient(
                    endpointType,
                    scope
                )
            )
            .baseUrl(endpointType.url)
            .build()
    }

    fun provideRetrofit(endpointType: EndpointType, scope: Scope): Retrofit {
        val reference = retrofitCache.get(endpointType)
        return if (reference != null) {
            Timber.tag(moduleTag).d(
                "Using cached retrofit instance for endpoint: ${endpointType.name}"
            )
            reference
        }
        else {
            Timber.tag(moduleTag).d(
                "Creating new retrofit instance for endpoint: ${endpointType.name}"
            )
            val retrofit =
                createRetrofit(
                    endpointType,
                    scope
                )
            retrofitCache.put(endpointType, retrofit)
            retrofit
        }
    }
}