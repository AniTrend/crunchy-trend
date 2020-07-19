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

package co.anitrend.support.crunchyroll.navigation.extensions

import android.content.Intent
import androidx.collection.LruCache
import androidx.fragment.app.Fragment
import co.anitrend.support.crunchyroll.navigation.contract.INavigationTarget
import timber.log.Timber

internal const val APPLICATION_PACKAGE_NAME = "co.anitrend.support.crunchyroll"

private const val moduleTag = "NavigationExtensions"

private val classMap = object: LruCache<String, Class<*>>(6) {
    /**
     * Called after a cache miss to compute a value for the corresponding key.
     * Returns the computed value or null if no value can be computed. The
     * default implementation returns null.
     *
     *
     * The method is called without synchronization: other threads may
     * access the cache while this method is executing.
     *
     *
     * If a value for `key` exists in the cache when this method
     * returns, the created value will be released with [.entryRemoved]
     * and discarded. This can occur when multiple threads request the same key
     * at the same time (causing multiple values to be created), or when one
     * thread calls [.put] while another is creating a value for the same
     * key.
     */
    override fun create(key: String): Class<*>? {
        Timber.tag(moduleTag).v("Creating after cache miss for package: $key")
        return Class.forName(key)
    }
}

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
    classMap.get(this)?.castOrNull()

internal fun <T : Fragment> String.loadFragmentOrNull(): T? =
    try {
        loadClassOrNull<T>()?.newInstance()
    } catch (e: ClassNotFoundException) {
        Timber.tag("loadFragmentOrNull").e(e)
        null
    }

/**
 * Builds a an intent path for the target
 */
fun INavigationTarget.forIntent() =
    "$APPLICATION_PACKAGE_NAME.$packageName.$className".loadIntentOrNull()

/**
 * Creates a fragment instance from intent
 */
fun INavigationTarget.forFragment() =
    forIntent()?.component?.className?.loadFragmentOrNull<Fragment>()

/**
 * Build fragment class from intent
 */
fun <T: Fragment> INavigationTarget.forFragment() =
    forIntent()?.component?.className?.loadClassOrNull<T>()