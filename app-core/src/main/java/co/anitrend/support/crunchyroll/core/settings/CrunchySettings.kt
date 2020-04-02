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
import co.anitrend.arch.extension.preference.*
import co.anitrend.arch.extension.preference.contract.ISupportPreference
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.core.extensions.NullableStringPreference
import co.anitrend.support.crunchyroll.core.settings.common.IConfigurationSettings
import co.anitrend.support.crunchyroll.core.settings.common.locale.ILocaleSettings
import co.anitrend.support.crunchyroll.core.settings.common.privacy.IPrivacySettings
import co.anitrend.support.crunchyroll.core.settings.common.theme.IThemeSettings
import co.anitrend.support.crunchyroll.core.util.locale.AniTrendLocale
import co.anitrend.support.crunchyroll.core.util.theme.AniTrendTheme
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings.Companion.INVALID_USER_ID

class CrunchySettings(context: Context) : SupportPreference(context),
    IAuthenticationSettings,
    IConfigurationSettings, IPrivacySettings {

    override var isNewInstallation by BooleanPreference(
        R.string.settings_is_new_installation,
        true,
        context.resources
    )

    override var versionCode by IntPreference(
        R.string.settings_version_code,
        0,
        context.resources
    )

    override var sessionId by NullableStringPreference(
        R.string.settings_authentication_session_id,
        null,
        context.resources
    )

    override var hasAccessToPremium by BooleanPreference(
        R.string.settings_authentication_premium_access,
        false,
        context.resources
    )

    override var authenticatedUserId by LongPreference(
        R.string.settings_authentication_id,
        INVALID_USER_ID,
        context.resources
    )

    override var isAuthenticated by BooleanPreference(
        R.string.settings_authentication_logged_in,
        false,
        context.resources
    )

    override var locale by EnumPreference(
        R.string.settings_configuration_locale,
        AniTrendLocale.AUTOMATIC,
        context.resources
    )

    override var theme by EnumPreference(
        R.string.settings_configuration_theme,
        AniTrendTheme.SYSTEM,
        context.resources
    )

    override var isAnalyticsEnabled by BooleanPreference(
        R.string.settings_privacy_usage_analytics,
        false,
        context.resources
    )

    override var isCrashlyticsEnabled by BooleanPreference(
        R.string.settings_privacy_crash_analytics,
        true,
        context.resources
    )

    companion object  {

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