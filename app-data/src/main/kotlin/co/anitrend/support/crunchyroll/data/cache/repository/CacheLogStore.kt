/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.data.cache.repository

import co.anitrend.support.crunchyroll.data.cache.datasource.local.CacheDao
import co.anitrend.support.crunchyroll.data.cache.entity.CacheLogEntity
import co.anitrend.support.crunchyroll.data.cache.helper.inPast
import co.anitrend.support.crunchyroll.data.cache.model.CacheRequest
import org.threeten.bp.Instant
import org.threeten.bp.temporal.TemporalAmount

internal open class CacheLogStore(
    private val request: CacheRequest,
    private val dao: CacheDao
) {
    private suspend fun getRequestInstant(entityId: Long): Instant? {
        return dao.getCacheLog(request, entityId)?.timestamp
    }

    suspend fun isRequestExpired(entityId: Long, threshold: TemporalAmount): Boolean {
        return isRequestBefore(entityId, threshold.inPast())
    }

    suspend fun hasBeenRequested(entityId: Long): Boolean = dao.countMatching(request, entityId) > 0

    suspend fun isRequestBefore(entityId: Long, instant: Instant): Boolean {
        return getRequestInstant(entityId)?.isBefore(instant) ?: true
    }

    suspend fun updateLastRequest(entityId: Long, timestamp: Instant = Instant.now()) {
        dao.upsert(
            CacheLogEntity(
                request = request,
                cacheItemId = entityId,
                timestamp = timestamp
            )
        )
    }

    suspend fun invalidateLastRequest(entityId: Long) = updateLastRequest(entityId, Instant.EPOCH)
}