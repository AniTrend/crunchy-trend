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

package co.anitrend.support.crunchyroll.data.session.repository

import co.anitrend.arch.data.repository.SupportRepository
import co.anitrend.support.crunchyroll.data.session.source.contract.SessionSource
import co.anitrend.support.crunchyroll.domain.session.entities.Session
import co.anitrend.support.crunchyroll.domain.session.repositories.ISessionRepository
import kotlinx.coroutines.flow.Flow

internal class SessionRepository(
    private val coreSource: SessionSource,
    private val normalSource: SessionSource,
    private val unblockedSource: SessionSource
) : SupportRepository(coreSource),
    ISessionRepository {

    /**
     * Returns a session that can be used when the user is not authenticated
     */
    override fun getCoreSession(): Flow<Session?> {
        return coreSource()
    }

    /**
     * Returns a session that behaves as a fallback when the unblock session fails
     */
    override fun getNormalSession(): Flow<Session?> {
        return normalSource()
    }

    /**
     * Returns a session for the authenticated user
     */
    override fun getUnblockedSession(): Flow<Session?> {
        return unblockedSource()
    }

    /**
     * Deals with cancellation of any pending or on going operations that the repository
     * might be working on
     */
    override fun onCleared() {
        super.onCleared()
        normalSource.cancelAllChildren()
        unblockedSource.cancelAllChildren()
    }
}