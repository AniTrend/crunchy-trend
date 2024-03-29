/*
 *    Copyright 2019 AniTrend
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required = applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package co.anitrend.support.crunchyroll.core.settings

import android.content.Context
import co.anitrend.arch.extension.preference.SupportPreference
import co.anitrend.arch.extension.preference.contract.ISupportPreference
import co.anitrend.arch.extension.settings.*
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.core.settings.common.IConfigurationSettings
import co.anitrend.support.crunchyroll.core.settings.common.cache.ICacheSettings
import co.anitrend.support.crunchyroll.core.settings.common.locale.ILocaleSettings
import co.anitrend.support.crunchyroll.core.settings.common.privacy.IPrivacySettings
import co.anitrend.support.crunchyroll.core.settings.common.theme.IThemeSettings
import co.anitrend.support.crunchyroll.core.util.locale.AniTrendLocale
import co.anitrend.support.crunchyroll.core.util.theme.AniTrendTheme
import co.anitrend.support.crunchyroll.data.arch.database.settings.IRefreshBehaviourSettings
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings
import co.anitrend.support.crunchyroll.data.authentication.settings.IAuthenticationSettings.Companion.INVALID_USER_ID

class CrunchySettings(context: Context) : SupportPreference(context),
    IAuthenticationSettings, IConfigurationSettings, IPrivacySettings,
    ICacheSettings, IRefreshBehaviourSettings {

    override val isNewInstallation = BooleanSetting(
        key = R.string.settings_is_new_installation,
        default = true,
        resources = context.resources,
        preference = this
    )

    override val versionCode = IntSetting(
        key = R.string.settings_version_code,
        default = 0,
        resources = context.resources,
        preference = this
    )

    override val sessionId = NullableStringSetting(
        key = R.string.settings_authentication_session_id,
        default = null,
        resources = context.resources,
        preference = this
    )

    override val hasAccessToPremium = BooleanSetting(
        key = R.string.settings_authentication_premium_access,
        default = false,
        resources = context.resources,
        preference = this
    )

    override val authenticatedUserId = LongSetting(
        key = R.string.settings_authentication_id,
        default = INVALID_USER_ID,
        resources = context.resources,
        preference = this
    )

    override val isAuthenticated = BooleanSetting(
        key = R.string.settings_authentication_logged_in,
        default = false,
        resources = context.resources,
        preference = this
    )

    override val locale = EnumSetting(
        key = R.string.settings_configuration_locale,
        default = AniTrendLocale.AUTOMATIC,
        resources = context.resources,
        preference = this
    )

    override val theme = EnumSetting(
        key = R.string.settings_configuration_theme,
        default = AniTrendTheme.SYSTEM,
        resources = context.resources,
        preference = this
    )

    override val isAnalyticsEnabled = BooleanSetting(
        key = R.string.settings_privacy_usage_analytics,
        default = false,
        resources = context.resources,
        preference = this
    )

    override val isCrashlyticsEnabled = BooleanSetting(
        key = R.string.settings_privacy_crash_analytics,
        default = true,
        resources = context.resources,
        preference = this
    )

    override val usageRatio = FloatSetting(
        key = R.string.settings_cache_maximum_usage_ratio,
        default = ICacheSettings.MINIMUM_CACHE_LIMIT,
        resources = context.resources,
        preference = this
    )

    override val clearDataOnSwipeRefresh = BooleanSetting(
        key = R.string.settings_refresh_behaviour,
        default = true,
        resources = context.resources,
        preference = this
    )

    companion object  {

        /**
         * Binding types for [CrunchySettings]
         */
        internal val BINDINGS = arrayOf(
            ISupportPreference::class, IConfigurationSettings::class,
            ILocaleSettings::class, IThemeSettings::class,
            IAuthenticationSettings::class, IPrivacySettings::class,
            ICacheSettings::class, IRefreshBehaviourSettings::class
        )
    }
}