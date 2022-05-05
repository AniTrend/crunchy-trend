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

package co.anitrend.support.crunchyroll.data.arch.database.extension

import co.anitrend.arch.extension.ext.empty

internal fun List<*>.toCommaSeparatedValues(): String {
    return if (isNotEmpty()) {
        joinToString(separator = ",")
    } else
        String.empty()
}

internal fun String.fromCommaSeparatedValues(): List<String> {
    return if (isNotBlank())
        split(',')
    else
        emptyList()
}

internal inline fun <reified T : Enum<T>> Int.toEnum(): T = enumValues<T>()[this]

internal inline fun <reified T: Enum<*>> String.toEnum(): T {
    val `class` = T::class.java
    return `class`.enumConstants?.first { it.name == this }!!
}

internal fun Enum<*>.fromEnum() = name