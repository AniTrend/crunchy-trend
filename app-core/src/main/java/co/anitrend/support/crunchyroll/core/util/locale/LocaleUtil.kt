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

package co.anitrend.core.util.locale

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import co.anitrend.support.crunchyroll.core.settings.common.locale.ILocaleSettings
import co.anitrend.support.crunchyroll.core.util.locale.AniTrendLocale
import java.util.*

class LocaleUtil(
    private val context: Context,
    private val settings: ILocaleSettings
) {

    val locale: Locale
        get() {
            if (settings.locale == AniTrendLocale.AUTOMATIC)
                return Locale.getDefault()
            return getPersonalizedLocale()
        }

    internal fun applyApplicationLocale() {
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun getPersonalizedLocale(): Locale {
        val locale = settings.locale
        if (locale.country == null)
            return Locale(locale.language)
        return Locale(locale.language, locale.language)
    }
}