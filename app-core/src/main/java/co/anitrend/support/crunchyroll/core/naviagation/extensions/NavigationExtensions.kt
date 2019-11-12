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

package co.anitrend.support.crunchyroll.core.naviagation.extensions

import android.content.Intent
import androidx.fragment.app.Fragment
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.naviagation.contract.INavigationTarget
import timber.log.Timber

private const val APPLICATION_PACKAGE_NAME = "co.anitrend.support.crunchyroll"

private val classMap = mutableMapOf<String, Class<*>>()

private inline fun <reified T : Any> Any.castOrNull() = this as? T

private fun forIntent(className: String): Intent =
    Intent(Intent.ACTION_VIEW)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .setClassName(APPLICATION_PACKAGE_NAME, className)

internal fun String.loadIntentOrNull(): Intent? =
    try {
        Class.forName(this).run {
            forIntent(this@loadIntentOrNull)
        }
    } catch (e: ClassNotFoundException) {
        Timber.tag("loadIntentOrNull").e(e)
        null
    }

@Throws(ClassNotFoundException::class)
internal fun <T> String.loadClassOrNull(): Class<out T>? =
    classMap.getOrPut(this) {
        Class.forName(this)
    }.castOrNull()

internal fun <T : SupportFragment<*, *, *>> String.loadFragmentOrNull(): T? =
    try {
        this.loadClassOrNull<T>()?.newInstance()
    } catch (e: ClassNotFoundException) {
        Timber.tag("loadFragmentOrNull").e(e)
        null
    }

/**
 * Builds a an intent path for the target
 */
fun INavigationTarget.forIntent(): Intent? {
    return "$APPLICATION_PACKAGE_NAME.$packageName.$className".loadIntentOrNull()
}

fun INavigationTarget.forFragment(): SupportFragment<*, *, *>? {
    return forIntent()?.component?.className?.loadFragmentOrNull()
}