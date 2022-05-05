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

package co.anitrend.support.crunchyroll.core.initializer.contract

import androidx.startup.Initializer
import co.anitrend.arch.extension.coroutine.ISupportCoroutine
import co.anitrend.arch.extension.coroutine.extension.Main
import co.anitrend.support.crunchyroll.core.initializer.InjectorInitializer

/**
 * Contract for feature initializer with coroutine support for deferred initialization
 */
abstract class AbstractFeatureInitializer<T> : Initializer<T>, ISupportCoroutine by Main() {
    /**
     * @return A list of dependencies that this [Initializer] depends on. This is
     * used to determine initialization order of [Initializer]s.
     *
     * By default a feature initializer should only start after koin has been initialized
     */
    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(InjectorInitializer::class.java)
    }
}