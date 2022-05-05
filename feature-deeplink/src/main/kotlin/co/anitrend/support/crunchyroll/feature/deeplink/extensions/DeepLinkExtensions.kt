/*
 *    Copyright 2021 AniTrend
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

package co.anitrend.support.crunchyroll.feature.deeplink.extensions

import androidx.fragment.app.FragmentActivity
import co.anitrend.arch.extension.ext.PUBLICATION
import co.anitrend.support.crunchyroll.data.util.extension.koinOf
import co.anitrend.support.crunchyroll.feature.deeplink.environment.AppEnvironment
import com.hellofresh.deeplink.*
import timber.log.Timber

/**
 * Lazy deep link parameters for fragment activities
 *
 * @param baseRoute Route to look up intent data
 * @param environment Current application environment
 *
 * @return [Lazy] of the target type
 *
 * @see FragmentActivity.getIntent
 * @see AppEnvironment
 */
inline fun <reified T> FragmentActivity.deepLink(
    baseRoute: BaseRoute<T>,
    environment: Environment = koinOf()
): Lazy<T?> = lazy(PUBLICATION) {
    val fallbackAction = object : Action<T?> {
        override fun run(
            uri: DeepLinkUri,
            params: Map<String, String>,
            env: Environment
        ): T? {
            Timber.w("Registered router failed to match: $uri")
            return null
        }
    }

    if (intent.data == null)
        return@lazy null

    val deepLinkParser = DeepLinkParser
        .of<T?>(environment)
        .addRoute(baseRoute)
        .addFallbackAction(fallbackAction)

    val uri = DeepLinkUri.parse(intent.data.toString())

    deepLinkParser.build().parse(uri)
}