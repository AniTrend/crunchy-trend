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

import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.ui.fragment.SupportFragment
import org.koin.androidx.scope.fragmentScope
import org.koin.core.component.KoinScopeComponent

abstract class CrunchyFragment : SupportFragment(), KoinScopeComponent {

    override val scope by fragmentScope()

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState(): ISupportViewModelState<*>? = null
}