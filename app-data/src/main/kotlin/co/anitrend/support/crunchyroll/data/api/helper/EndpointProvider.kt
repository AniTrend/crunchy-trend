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

import co.anitrend.arch.extension.LAZY_MODE_SYNCHRONIZED
import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.api.converter.CrunchyConverterFactory
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyRequestInterceptor
import co.anitrend.support.crunchyroll.data.api.interceptor.CrunchyResponseInterceptor
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.parameter.DefinitionParameters
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import retrofit2.Retrofit
import timber.log.Timber
import java.util.HashMap

internal object EndpointProvider {

    private val module = javaClass.simpleName
    private val retrofitInstances by lazy(LAZY_MODE_SYNCHRONIZED) {
        HashMap<String, Retrofit>()
    }

    private fun provideOkHttpClient(endpointType: EndpointType, scope: Scope) : OkHttpClient {
        val builder = scope.get<OkHttpClient.Builder> {
            parametersOf(
                when (endpointType) {
                    EndpointType.SESSION,
                    EndpointType.AUTH,
                    EndpointType.JSON -> HttpLoggingInterceptor.Level.BODY
                    else -> HttpLoggingInterceptor.Level.HEADERS
                }
            )
        }

        if (endpointType == EndpointType.JSON) {
            Timber.tag(module).d(
                "Building additional interceptors for request: ${endpointType.name}"
            )
            builder.addInterceptor(
                CrunchyRequestInterceptor(
                    authentication = scope.get()
                )
            ).addInterceptor(
                CrunchyResponseInterceptor(
                    authentication = scope.get(),
                    connectivityHelper = scope.get(),
                    dispatchers = scope.get()
                )
            )
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
        return if (retrofitInstances.containsKey(endpointType.name)) {
            Timber.tag(module).d("Using cached retrofit instance for endpoint: ${endpointType.name}")
            retrofitInstances[endpointType.name]!!
        }
        else {
            Timber.tag(module).d("Creating new retrofit instance for endpoint: ${endpointType.name}")
            val retrofit = createRetrofit(endpointType, scope)
            retrofitInstances[endpointType.name] = retrofit
            retrofit
        }
    }
}