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

package co.anitrend.support.crunchyroll.core.settings

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.edit
import co.anitrend.arch.extension.preference.SupportPreference
import co.anitrend.arch.extension.preference.contract.ISupportPreference
import co.anitrend.support.crunchyroll.core.util.locale.AniTrendLocale
import co.anitrend.support.crunchyroll.core.util.theme.AniTrendTheme
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.core.settings.common.IConfigurationSettings
import co.anitrend.support.crunchyroll.core.settings.common.locale.ILocaleSettings
import co.anitrend.support.crunchyroll.core.settings.common.privacy.IPrivacySettings
import co.anitrend.support.crunchyroll.core.settings.common.theme.IThemeSettings
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings.Companion.INVALID_USER_ID

class CrunchySettings(context: Context) : SupportPreference(context),
    IAuthenticationSettings,
    IConfigurationSettings, IPrivacySettings {

    override var sessionId: String? = null
        get() = sharedPreferences.getString(
            stringOf(R.string.settings_authentication_session_id),
            null
        )
        set(value) {
            field = value
            sharedPreferences.edit(commit = true) {
                putString(
                    stringOf(R.string.settings_authentication_session_id),
                    value
                )
            }
        }

    override var hasAccessToPremium: Boolean = false
        get() = sharedPreferences.getBoolean(
            stringOf(R.string.settings_authentication_premium_access),
            false
        )
        set(value) {
            field = value
            sharedPreferences.edit {
                putBoolean(
                    stringOf(R.string.settings_authentication_premium_access),
                    value
                )
            }
        }

    override var authenticatedUserId: Int = INVALID_USER_ID
        get() = sharedPreferences.getInt(
            stringOf(R.string.settings_authentication_id),
            INVALID_USER_ID
        )
        set(value) {
            field = value
            sharedPreferences.edit {
                putInt(
                    stringOf(R.string.settings_authentication_id),
                    value
                )
            }
        }

    override var isAuthenticated: Boolean = false
        get() = sharedPreferences.getBoolean(
            stringOf(R.string.settings_authentication_logged_in),
            false
        )
        set(value) {
            field = value
            sharedPreferences.edit {
                putBoolean(
                    stringOf(R.string.settings_authentication_logged_in),
                    value
                )
            }
        }

    override var locale: AniTrendLocale = AniTrendLocale.AUTOMATIC
        get() = AniTrendLocale.valueOf(
            sharedPreferences.getString(
                stringOf(R.string.settings_configuration_locale),
                null
            ) ?: AniTrendLocale.AUTOMATIC.name
        )
        set(value) {
            field = value
            sharedPreferences.edit {
                putString(
                    stringOf(R.string.settings_configuration_locale),
                    value.name
                )
            }
        }

    override var theme: AniTrendTheme = AniTrendTheme.SYSTEM
        get() = AniTrendTheme.valueOf(
            sharedPreferences.getString(
                stringOf(R.string.settings_configuration_theme),
                null
            ) ?: AniTrendTheme.SYSTEM.name
        )
        set(value) {
            field = value
            sharedPreferences.edit {
                putString(
                    stringOf(R.string.settings_configuration_theme),
                    value.name
                )
            }
        }

    override var isAnalyticsEnabled: Boolean = false
        get() = sharedPreferences.getBoolean(
            stringOf(R.string.settings_privacy_usage_analytics),
            false
        )
        set(value) {
            field = value
            sharedPreferences.edit {
                putBoolean(
                    stringOf(R.string.settings_privacy_usage_analytics),
                    value
                )
            }
        }

    override var isCrashlyticsEnabled: Boolean = true
        get() = sharedPreferences.getBoolean(
            stringOf(R.string.settings_privacy_crash_analytics),
            true
        )
        set(value) {
            field = value
            sharedPreferences.edit {
                putBoolean(
                    stringOf(R.string.settings_privacy_crash_analytics),
                    value
                )
            }
        }

    companion object  {
        private fun CrunchySettings.stringOf(
            @StringRes resource: Int
        ) = context.getString(resource)

        /**
         * Binding types for [CrunchySettings]
         */
        internal val BINDINGS = arrayOf(
            ISupportPreference::class, IConfigurationSettings::class,
            ILocaleSettings::class, IThemeSettings::class,
            IAuthenticationSettings::class, IPrivacySettings::class
        )
    }
}