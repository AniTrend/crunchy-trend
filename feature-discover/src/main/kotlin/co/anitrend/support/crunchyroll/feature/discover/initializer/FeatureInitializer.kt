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

package co.anitrend.support.crunchyroll.feature.discover.initializer

import android.content.Context
import co.anitrend.support.crunchyroll.core.initializer.contract.AbstractFeatureInitializer
import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper.Companion.loadModules
import co.anitrend.support.crunchyroll.feature.discover.koin.moduleHelper
import kotlinx.coroutines.launch

class FeatureInitializer : AbstractFeatureInitializer<Unit>() {
    /**
     * Initializes and a component given the application [Context]
     *
     * @param context The application context.
     */
    override fun create(context: Context) {
        moduleHelper.loadModules()
    }
}