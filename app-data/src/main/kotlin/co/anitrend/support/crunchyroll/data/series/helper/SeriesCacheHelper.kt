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

package co.anitrend.support.crunchyroll.data.series.helper

import co.anitrend.support.crunchyroll.data.cache.datasource.local.CacheDao
import co.anitrend.support.crunchyroll.data.cache.helper.instantInPast
import co.anitrend.support.crunchyroll.data.cache.model.CacheRequest
import co.anitrend.support.crunchyroll.data.cache.repository.CacheLogStore
import org.threeten.bp.Instant

internal data class SeriesCacheHelper(
    val dao: CacheDao
) : CacheLogStore(CacheRequest.SERIES, dao) {

    suspend fun shouldRefreshSeries(
        seriesId: Long,
        expiresAfter: Instant = instantInPast(days = 2)
    ): Boolean = isRequestBefore(seriesId, expiresAfter)
}