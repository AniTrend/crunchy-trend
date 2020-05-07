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

package co.anitrend.support.crunchyroll.koin

import co.anitrend.arch.core.analytic.contract.ISupportAnalytics
import co.anitrend.support.crunchyroll.analytics.AnalyticsLogger
import co.anitrend.support.crunchyroll.core.koin.coreModules
import co.anitrend.support.crunchyroll.core.settings.common.privacy.IPrivacySettings
import co.anitrend.support.crunchyroll.data.arch.di.crunchDataModules
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val analyticsModule = module {
    factory<ISupportAnalytics> {
        val privacySettings = get<IPrivacySettings>()

        val analytics = FirebaseAnalytics.getInstance(androidContext())
        analytics.setAnalyticsCollectionEnabled(privacySettings.isAnalyticsEnabled)

        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCrashlyticsCollectionEnabled(privacySettings.isCrashlyticsEnabled)

        AnalyticsLogger(
            analytics,
            crashlytics
        )
    }
}


val appModules = listOf(analyticsModule) + coreModules + crunchDataModules