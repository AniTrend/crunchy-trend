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

package co.anitrend.support.crunchyroll.data.auth.model.contract

import co.anitrend.support.crunchyroll.data.arch.ISO8601Date
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser
import co.anitrend.support.crunchyroll.data.util.extension.iso8601ToUnixTime

interface ICrunchySessionUser {
    val user: CrunchyUser
    val auth: String
    val expires: ISO8601Date

    fun hasExpired() : Boolean {
        val expiryTime = expires.iso8601ToUnixTime() ?: 0
        val currentSystemTime = System.currentTimeMillis()
        return currentSystemTime >= expiryTime
    }
}