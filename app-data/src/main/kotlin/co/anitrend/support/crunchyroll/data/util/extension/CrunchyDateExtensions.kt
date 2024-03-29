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

package co.anitrend.support.crunchyroll.data.util.extension

import co.anitrend.arch.extension.ext.PUBLICATION
import co.anitrend.arch.extension.util.date.contract.AbstractSupportDateHelper
import co.anitrend.support.crunchyroll.data.arch.ISO8601Date
import co.anitrend.support.crunchyroll.data.arch.RCF822Date
import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyProperty
import co.anitrend.support.crunchyroll.data.util.CrunchyDateUtil.Companion.ISO8601_PATTERN
import co.anitrend.support.crunchyroll.data.util.CrunchyDateUtil.Companion.RCF822_PATTERN
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import timber.log.Timber
import kotlin.jvm.Throws

/**
 * Helper to resolve koin dependencies
 *
 * @param qualifier Help qualify a component
 * @param parameters Help define a DefinitionParameters
 *
 * @return [T]
 */
inline fun <reified T> koinOf(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): T {
    val koin = GlobalContext.get()
    return koin.get(qualifier, parameters)
}

/**
 * Retrieve a property
 *
 * @throws IllegalArgumentException when [property] key cannot be found
 */
@Throws(IllegalArgumentException::class)
fun requireProperty(property: CrunchyProperty): String {
    val koin = GlobalContext.get()
    val prop = koin.getProperty<String>(property.key)
    return requireNotNull(prop)
}

val supportDateHelper by lazy(PUBLICATION) {
    koinOf<AbstractSupportDateHelper>()
}

internal fun ISO8601Date.iso8601ToUnixTime() =
    runCatching{
        supportDateHelper.convertToUnixTimeStamp(
            originDate = this,
            inputPattern = ISO8601_PATTERN
        )
    }.getOrElse {
        Timber.tag("iso8601ToUnixTime").e(it)
        0
    }

internal fun RCF822Date.rcf822ToUnixTime() =
    runCatching {
        supportDateHelper.convertToUnixTimeStamp(
            originDate = this,
            dateTimeFormatter = RCF822_PATTERN
        )
    }.getOrElse {
        Timber.tag("rcf822ToUnixTime").e(it)
        0
    }