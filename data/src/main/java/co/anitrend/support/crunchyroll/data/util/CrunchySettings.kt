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

package co.anitrend.support.crunchyroll.data.util

import android.content.Context
import androidx.core.content.edit
import io.wax911.support.extension.preference.SupportPreference

class CrunchySettings(context: Context): SupportPreference(context) {

    var authenticatedUserId: Int = INVALID_USER_ID
        get() = sharedPreferences.getInt(AUTHENTICATED_USER, -1)
        set(value) {
            field = value
            sharedPreferences.edit { AUTHENTICATED_USER to value }
        }

    companion object  {
        const val INVALID_USER_ID: Int = -1
        private const val AUTHENTICATED_USER = "_authenticatedUser"
    }
}