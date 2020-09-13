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

package co.anitrend.support.crunchyroll.core.ui.fragment

import android.os.Bundle
import android.view.View
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.ui.fragment.SupportFragment
import org.koin.android.ext.android.getKoin
import org.koin.androidx.scope.ScopeActivity
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

abstract class CrunchyFragment : SupportFragment(), KoinScopeComponent {

    private val scopeID: ScopeID by lazy(UNSAFE) { getScopeId() }
    override val koin by lazy(UNSAFE) { getKoin() }
    override val scope: Scope by lazy(UNSAFE) {
        koin.createScope(scopeID, getScopeName(), this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runCatching {
            koin._logger.debug("Open fragment scope: $scope")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching {
            koin._logger.debug("Close fragment scope: $scope")
            scope.close()
        }
    }

    /**
     * inject lazily
     * @param qualifier - bean qualifier / optional
     * @param parameters - injection parameters
     */
    inline fun <reified T : Any> inject(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
    ) = lazy(UNSAFE) { get<T>(qualifier, parameters) }

    /**
     * get given dependency
     * @param name - bean name
     * @param parameters - injection parameters
     */
    inline fun <reified T : Any> get(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
    ): T = runCatching {
        scope.get<T>(qualifier, parameters)
    }.getOrElse {
        koin.get(qualifier, parameters)
    }
}