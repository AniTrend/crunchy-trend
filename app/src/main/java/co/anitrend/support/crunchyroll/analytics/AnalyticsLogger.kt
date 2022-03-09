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

package co.anitrend.support.crunchyroll.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import co.anitrend.arch.core.analytic.contract.ISupportAnalytics
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.extension.ext.empty
import co.anitrend.support.crunchyroll.core.settings.common.privacy.IPrivacySettings
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class AnalyticsLogger(
    context: Context,
    settings: IPrivacySettings
) : Timber.Tree(), ISupportAnalytics {

    private val analytics by lazy(UNSAFE) {
        FirebaseApp.getApps(context).let {
            if (it.isNotEmpty())
                FirebaseAnalytics.getInstance(context).apply {
                    setAnalyticsCollectionEnabled(
                        settings.isAnalyticsEnabled.value
                    )
                }
            else
                null
        }
    }

    private val crashlytics by lazy(UNSAFE) {
        FirebaseApp.getApps(context).let {
            if (it.isNotEmpty())
                FirebaseCrashlytics.getInstance().apply {
                    setCrashlyticsCollectionEnabled(
                        settings.isCrashlyticsEnabled.value
                    )
                }
            else
                null
        }
    }

    /**
     * Write a log message to its destination. Called for all level-specific methods by default.
     *
     * @param priority Log level. See [Log] for constants.
     * @param tag Explicit or inferred tag. May be `null`.
     * @param message Formatted log message. May be `null`, but then `t` will not be.
     * @param t Accompanying exceptions. May be `null`, but then `message` will not be.
     */
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority < Log.INFO)
            return

        runCatching {
            crashlytics?.setCustomKey(PRIORITY, priority)
            crashlytics?.setCustomKey(TAG, tag ?: "")
            crashlytics?.setCustomKey(MESSAGE, message)
        }.exceptionOrNull()?.printStackTrace()

        runCatching {
            if (t == null) log(priority, tag, message)
            else logException(t)
        }
    }

    override fun logCurrentScreen(context: FragmentActivity, tag : String) {
        runCatching {
            // Do nothing
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun logCurrentState(tag: String, bundle: Bundle?) {
        runCatching {
            bundle?.also { analytics?.logEvent(tag, it) }
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun logException(throwable: Throwable) {
        runCatching {
            crashlytics?.recordException(throwable)
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun log(priority: Int, tag: String?, message: String) {
        runCatching {
            crashlytics?.log(message)
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun clearCrashAnalyticsSession() {
        runCatching {
            analytics?.resetAnalyticsData()
            analytics?.setUserId(String.empty())
            crashlytics?.setUserId(String.empty())
        }.onFailure {
            it.printStackTrace()
        }
    }

    override fun setCrashAnalyticIdentifier(identifier: String) {
        runCatching {
            analytics?.setUserId(identifier)
            crashlytics?.setUserId(identifier)
        }.onFailure {
            it.printStackTrace()
        }
    }

    companion object {
        private const val PRIORITY = "priority"
        private const val TAG = "tag"
        private const val MESSAGE = "message"
    }
}