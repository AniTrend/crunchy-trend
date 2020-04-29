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

import co.anitrend.arch.data.factory.SupportEndpointFactory
import retrofit2.Retrofit
import kotlin.reflect.KClass

/**
 * Generates retrofit service classes
 *
 * @param endpoint The interface class method representing your request to use
 */
@Deprecated(
    "We won't be using this anymore, instead we'll be using a custom class",
    ReplaceWith(
        expression = "EndpointProvider",
        imports = arrayOf("co.anitrend.support.crunchyroll.data.api.provider.EndpointProvider")
    )
)
open class CrunchyEndpointFactory<S: Any>(
    endpoint: KClass<S>,
    override val retrofit: Retrofit
) : SupportEndpointFactory<S>("", endpoint)