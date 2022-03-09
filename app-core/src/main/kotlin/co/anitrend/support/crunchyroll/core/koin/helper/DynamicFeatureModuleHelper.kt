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

package co.anitrend.support.crunchyroll.core.koin.helper

import androidx.lifecycle.LifecycleOwner
import co.anitrend.arch.extension.lifecycle.SupportLifecycle
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import timber.log.Timber

/**
 * Module loader helper for dynamic features
 */
class DynamicFeatureModuleHelper(
    private val modules: List<Module>
) : SupportLifecycle {


    /**
     * Notifies that `ON_CREATE` event occurred.
     *
     * This method will be called after the [LifecycleOwner]'s `onCreate`
     * method returns.
     *
     * @param owner the component, whose state was changed
     */
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        Timber.v(
            "Loading ${modules.size} feature modules"
        )
        loadModules()
    }

    /**
     * Notifies that `ON_DESTROY` event occurred.
     *
     * This method will be called before the [LifecycleOwner]'s `onDestroy` method
     * is called.
     *
     * @param owner the component, whose state was changed
     */
    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        Timber.v(
            "Unloading ${modules.size} feature modules"
        )
        unloadModules()
    }

    companion object {
        fun DynamicFeatureModuleHelper.loadModules() = loadKoinModules(modules)
        fun DynamicFeatureModuleHelper.unloadModules() = unloadKoinModules(modules)
    }
}