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

package co.anitrend.support.crunchyroll.initializer

import android.content.Context
import androidx.startup.Initializer
import co.anitrend.support.crunchyroll.core.initializer.InjectorInitializer
import co.anitrend.support.crunchyroll.core.initializer.contract.AbstractInitializer
import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper.Companion.loadModules
import co.anitrend.support.crunchyroll.koin.appModules

class ApplicationInitializer : AbstractInitializer<Unit>() {

    /**
     * Initializes and a component given the application [Context]
     *
     * @param context The application context.
     */
    override fun create(context: Context) {
        appModules.loadModules()
    }

    /**
     * @return A list of dependencies that this [Initializer] depends on. This is
     * used to determine initialization order of [Initializer]s.
     *
     * For e.g. if a [Initializer] `B` defines another
     * [Initializer] `A` as its dependency, then `A` gets initialized before `B`.
     */
    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(InjectorInitializer::class.java)
}