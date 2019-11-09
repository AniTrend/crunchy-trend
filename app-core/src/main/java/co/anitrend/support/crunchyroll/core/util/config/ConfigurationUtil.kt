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

package co.anitrend.support.crunchyroll.core.util.config

import android.content.Intent
import co.anitrend.support.crunchyroll.core.util.config.contract.IConfigurationUtil
import co.anitrend.support.crunchyroll.core.util.locale.AniTrendLocale
import co.anitrend.core.util.locale.LocaleUtil
import co.anitrend.support.crunchyroll.core.util.theme.AniTrendTheme
import co.anitrend.core.util.theme.ThemeUtil
import co.anitrend.support.crunchyroll.core.settings.common.IConfigurationSettings
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity

/**
 * Configuration helper for the application
 */
class ConfigurationUtil(
    private val settings: IConfigurationSettings
) : IConfigurationUtil {

    override val moduleTag: String = this::class.java.simpleName

    // we might change this in future when we have more themes
    private var applicationTheme = AniTrendTheme.SYSTEM
    private var applicationLocale = AniTrendLocale.AUTOMATIC

    /**
     * Applies configuration upon the create state of the current activity
     *
     * @param activity
     */
    override fun onCreate(activity: CrunchyActivity<*, *>) {
        applicationTheme = settings.theme
        applicationLocale = settings.locale
        LocaleUtil(activity, settings).applyApplicationLocale()
        ThemeUtil(settings).applyApplicationTheme(activity)
    }

    /**
     * Applies configuration upon the resume state of the current activity
     *
     * @param activity
     */
    override fun onResume(activity: CrunchyActivity<*, *>) {
        if (applicationTheme != settings.theme) {
            val intent: Intent? = activity.intent
            with(activity) {
                finish()
                this()
                startActivity(intent)
                this()
            }
        }
    }

    companion object {
        private operator fun CrunchyActivity<*, *>.invoke() {
            overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
        }
    }
}