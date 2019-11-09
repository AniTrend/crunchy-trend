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

package co.anitrend.support.crunchyroll.core.analytics

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import co.anitrend.arch.core.analytic.contract.ISupportAnalytics
import timber.log.Timber

class AnalyticsLogger(): Timber.Tree(), ISupportAnalytics {

    /**
     * Write a log message to its destination. Called for all level-specific methods by default.
     *
     * @param priority Log level. See [Log] for constants.
     * @param tag Explicit or inferred tag. May be `null`.
     * @param message Formatted log message. May be `null`, but then `t` will not be.
     * @param throwable Accompanying exceptions. May be `null`, but then `message` will not be.
     */
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority < Log.INFO)
            return

        when (throwable) {
            null -> super.log(priority, message)
            else -> super.log(priority, message, throwable)
        }
    }

    override fun logCurrentScreen(context: FragmentActivity, tag : String) {

    }

    override fun logCurrentState(tag: String, bundle: Bundle?) {

    }

    override fun logException(throwable: Throwable) {

    }

    override fun log(priority: Int, tag: String?, message: String) {

    }

    override fun clearUserSession() {

    }

    override fun setCrashAnalyticUser(userIdentifier: String) {

    }

    override fun resetAnalyticsData() {

    }
}