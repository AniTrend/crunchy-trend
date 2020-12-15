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

package co.anitrend.support.crunchyroll.navigation.contract

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import org.koin.core.component.KoinComponent

abstract class NavigationRouter : KoinComponent {
    private val moduleTag = javaClass.simpleName

    /**
     * Feature provider contract
     */
    internal abstract val provider: INavigationProvider

    /**
     * Key for bundles
     */
    val extraKey = "$moduleTag.extraKey"

    /**
     * Starts a new activity
     */
    operator fun invoke(
        context: Context?,
        payload: Parcelable? = null,
        flags: Int = Intent.FLAG_ACTIVITY_NEW_TASK,
        action: String = Intent.ACTION_VIEW,
        options: Bundle? = null
    ) {
        runCatching {
            val intent = provider.activity(context)
            intent?.flags = flags
            intent?.action = action
            intent?.putExtra(extraKey, payload)
            context?.startActivity(intent, options)
        }.exceptionOrNull()?.printStackTrace()
    }
}