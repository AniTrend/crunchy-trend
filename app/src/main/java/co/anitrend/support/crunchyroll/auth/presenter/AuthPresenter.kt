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

package co.anitrend.support.crunchyroll.auth.presenter

import android.content.Context
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.data.util.CrunchySettings
import co.anitrend.support.crunchyroll.domain.entities.result.user.User

class AuthPresenter(
    context: Context,
    settings: CrunchySettings
) : CrunchyCorePresenter(context, settings) {

    fun onLoginStateChange(user: User?) {
        supportPreference.authenticatedUserId = user?.userId ?: CrunchySettings.INVALID_USER_ID
        supportPreference.hasAccessToPremium = user?.premium != null
        supportPreference.isAuthenticated = user != null
    }
}