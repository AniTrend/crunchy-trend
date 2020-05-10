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

import android.content.Context
import co.anitrend.arch.extension.attachComponent
import co.anitrend.arch.extension.detachComponent
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.ui.contract.IFeatureContract

abstract class CrunchyFragment : SupportFragment(), IFeatureContract {

    /**
     * Called when a fragment is first attached to its context.
     * [onCreate] will be called after this.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        featureModuleHelper()?.also {
            attachComponent(it)
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity. This
     * is called after [onDestroy].
     */
    override fun onDetach() {
        featureModuleHelper()?.also {
            detachComponent(it)
        }
        super.onDetach()
    }
}