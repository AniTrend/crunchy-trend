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
import androidx.fragment.app.Fragment

/**
 * Fragment factory to help us construct new fragments
 *
 * @property FRAGMENT_TAG unique tag that can be used by
 * [androidx.fragment.app.FragmentManager]
 *
 * @param T type of your fragment
 */
interface IFragmentFactory<T: Fragment> {
    val FRAGMENT_TAG: String

    fun newInstance(bundle: Bundle? = null): T
}