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

package co.anitrend.support.crunchyroll.data.repository.session

import androidx.annotation.StringDef

@StringDef(
    SessionRequestType.START_UNBLOCK_SESSION,
    SessionRequestType.START_CORE_SESSION
)
annotation class SessionRequestType {
    companion object {
        const val START_UNBLOCK_SESSION = "START_UNBLOCK_SESSION"
        const val START_CORE_SESSION = "START_CORE_SESSION"
    }
}