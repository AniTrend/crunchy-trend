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

package co.anitrend.support.crunchyroll.core.util.theme

import android.os.Build
import android.view.View
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import co.anitrend.arch.extension.ext.getCompatColor
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.core.settings.common.theme.IThemeSettings
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.core.util.theme.AniTrendTheme

class ThemeUtil(private val settings: IThemeSettings) {

    @get:StyleRes
    val theme: Int
        get() {
            if (
                settings.theme.value == AniTrendTheme.SYSTEM ||
                settings.theme.value == AniTrendTheme.AMOLED ||
                settings.theme.value == AniTrendTheme.LIGHT
            ) return R.style.AppTheme

            return getPersonalizedTheme()
        }


    @Suppress("DEPRECATION")
    private fun CrunchyActivity<*>.applyWindowStyle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val systemUiOptions = window.decorView.systemUiVisibility
            when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_NO -> {
                    window.navigationBarColor = window.context.getCompatColor(R.color.colorPrimary)
                    window.decorView.systemUiVisibility = systemUiOptions or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
                AppCompatDelegate.MODE_NIGHT_YES -> {
                    window.navigationBarColor = window.context.getCompatColor(R.color.colorPrimary)
                    window.decorView.systemUiVisibility = systemUiOptions and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    window.decorView.systemUiVisibility = systemUiOptions and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
                else -> {
                    // According to Google/IO other ui options like auto and follow system might be deprecated
                    window.navigationBarColor = window.context.getCompatColor(R.color.colorPrimary)
                    window.decorView.systemUiVisibility = systemUiOptions or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }
        }
    }

    internal fun applyNightMode() {
        val theme = settings.theme
        if (theme.value == AniTrendTheme.SYSTEM)
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        else
            when (isThemeLight(settings.theme.value)) {
                true -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                else -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            }
    }

    internal fun applyApplicationTheme(context: CrunchyActivity<*>) {
        context.applyWindowStyle()
        context.setTheme(theme)
    }

    private fun isThemeLight(theme: AniTrendTheme): Boolean {
        return when (theme) {
            AniTrendTheme.AMOLED,
            AniTrendTheme.DARK -> false
            else -> true
        }
    }

    @StyleRes
    private fun getPersonalizedTheme(): Int {
        if (settings.theme.value == AniTrendTheme.DARK)
            TODO("Set up other custom themes")

        return 0
    }
}