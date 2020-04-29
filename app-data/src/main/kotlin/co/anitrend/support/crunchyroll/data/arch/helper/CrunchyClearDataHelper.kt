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

package co.anitrend.support.crunchyroll.data.arch.helper

import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import timber.log.Timber

/**
 * Helper for managing database on clear requests
 */
internal object CrunchyClearDataHelper {

    private val moduleTag = javaClass.simpleName

    /**
     * Perform checks and invoke [action]
     *
     * @param settings settings related to behaviour
     * @param connectivity connectivity helper
     * @param action what needs to be performed when all settings permit the operation
     */
    suspend inline operator fun invoke(
        settings: IRefreshBehaviourSettings,
        connectivity: SupportConnectivity,
        crossinline action: suspend () -> Unit
    ) {
        if (settings.clearDataOnSwipeRefresh) {
            if (connectivity.isConnected)
                runCatching {
                    action()
                }.exceptionOrNull()?.also {
                    Timber.tag(moduleTag).e(it)
                }
            return
        }

        Timber.tag(moduleTag).v(
            "Database table will not be cleared, setting prohibiting this"
        )
    }
}