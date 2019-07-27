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

package co.anitrend.support.crunchyroll.core.presenter

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import co.anitrend.support.crunchyroll.core.worker.CoreSessionWorker
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyLogin
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import io.wax911.support.core.presenter.SupportPresenter

class CrunchyCorePresenter(
    context: Context,
    settings: CrunchySettings
) : SupportPresenter<CrunchySettings>(context, settings) {

    val coreSessionRequest = OneTimeWorkRequest.Builder(
        CoreSessionWorker::class.java
    ).addTag(CoreSessionWorker.TAG).build()

    fun startSessionWorker() : WorkManager {
        return WorkManager.getInstance(context).also {
            it.enqueue(coreSessionRequest)
        }
    }

    fun onLoginSuccess(crunchyLogin: CrunchyLogin) {
        if (!supportPreference.isAuthenticated) {
            supportPreference.authenticatedUserId = crunchyLogin.user.user_id
            supportPreference.isAuthenticated = true
        } else {
            supportPreference.authenticatedUserId = CrunchySettings.INVALID_USER_ID
            supportPreference.isAuthenticated = false
        }
        startSessionWorker()
    }
}