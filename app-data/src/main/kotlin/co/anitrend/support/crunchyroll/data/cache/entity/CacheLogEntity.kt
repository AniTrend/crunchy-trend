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

package co.anitrend.support.crunchyroll.data.cache.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.cache.model.CacheRequest
import org.threeten.bp.Instant

@Entity(
    indices = [
        Index(value = ["cacheItemId"], unique = true)
    ]
)
internal data class CacheLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val request: CacheRequest,
    val cacheItemId: Long,
    val timestamp: Instant
) {
    companion object {
        fun new(
            request: CacheRequest,
            cacheItemId: Long
        ): CacheLogEntity = CacheLogEntity(
            request = request,
            cacheItemId = cacheItemId,
            timestamp = Instant.now()
        )
    }
}