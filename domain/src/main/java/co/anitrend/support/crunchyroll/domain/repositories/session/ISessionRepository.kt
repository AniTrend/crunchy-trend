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

package co.anitrend.support.crunchyroll.domain.repositories.session

import co.anitrend.support.crunchyroll.domain.entities.result.session.Session

interface ISessionRepository {

    /**
     * Returns a session that can be used when the user is not authenticated
     */
    suspend fun getCoreSession(): Session?

    /**
     * Returns a session that behaves as a fallback when the unblock session fails
     */
    suspend fun getNormalSession(): Session?

    /**
     * Returns a session for the authenticated user
     */
    suspend fun getUnblockedSession(): Session?
}