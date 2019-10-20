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
import co.anitrend.arch.extension.preference.SupportPreference

class CrunchySettings(context: Context): SupportPreference(context) {

    var authenticatedUserId: Int = INVALID_USER_ID
        get() = sharedPreferences.getInt(AUTHENTICATED_USER, -1)
        set(value) {
            field = value
            sharedPreferences.edit {
                putInt(AUTHENTICATED_USER, value)
            }
        }

    var isLightTheme: Boolean = true
        get() = sharedPreferences.getBoolean(IS_LIGHT_THEME, true)
        set(value) {
            field = value
            sharedPreferences.edit {
                putBoolean(IS_LIGHT_THEME, value)
            }
        }

    var sessionId: String? = null
        get() = sharedPreferences.getString(SESSION_ID, null)
        set(value) {
            field = value
            sharedPreferences.edit(commit = true) {
                putString(SESSION_ID, value)
            }
        }

    var hasAccessToPremium: Boolean = false
        get() = sharedPreferences.getBoolean(PREMIUM_ACCESS, false)
        set(value) {
            field = value
            sharedPreferences.edit {
                putBoolean(PREMIUM_ACCESS, value)
            }
        }

    var isAuthenticated: Boolean = false
        get() = sharedPreferences.getBoolean(IS_AUTHENTICATED, false)
        set(value) {
            field = value
            sharedPreferences.edit {
                putBoolean(IS_AUTHENTICATED, value)
            }
        }

    companion object  {
        const val INVALID_USER_ID: Int = -1
        private const val AUTHENTICATED_USER = "_authenticatedUser"
        private const val IS_AUTHENTICATED = "_isAuthenticated"
        private const val IS_LIGHT_THEME = "_isLightTheme"
        private const val SESSION_ID = "_sessionId"
        private const val PREMIUM_ACCESS = "_premiumAccess"
    }
}