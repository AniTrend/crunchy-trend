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

package co.anitrend.support.crunchyroll.data.mapper.rss

import co.anitrend.arch.data.mapper.SupportResponseMapper
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyRssMapper
import co.anitrend.support.crunchyroll.data.datasource.local.rss.CrunchyRssMediaDao
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssMedia
import co.anitrend.support.crunchyroll.data.model.rss.contract.ICrunchyRssChannel
import co.anitrend.support.crunchyroll.data.util.extension.rcf822ToUnixTime

class MediaListingResponseMapper(
    private val dao: CrunchyRssMediaDao
) : CrunchyRssMapper<CrunchyRssMedia>() {

    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     * called in [retrofit2.Callback.onResponse] after assuring that the response was a success
     *
     * @param source the incoming data source type
     * @return Mapped object that will be consumed by [onResponseDatabaseInsert]
     * @see [SupportResponseMapper]
     */
    override suspend fun onResponseMapFrom(source: ICrunchyRssChannel<CrunchyRssMedia>): List<CrunchyRssMedia> {
        return source.item?.map {
            it.copy(
                copyright = source.copyright,
                freeAvailableTime = it.freeAvailableDate.rcf822ToUnixTime(),
                premiumAvailableTime = it.premiumAvailableDate.rcf822ToUnixTime()
            )
        } ?: emptyList()
    }

    /**
     * Inserts the given object into the implemented room database,
     * called in [retrofit2.Callback.onResponse]
     *
     * @param mappedData mapped object from [onResponseMapFrom] to insert into the database
     * @see [SupportResponseMapper]
     */
    override suspend fun onResponseDatabaseInsert(mappedData: List<CrunchyRssMedia>) {
        if (mappedData.isNotEmpty())
            dao.upsert(mappedData)
    }
}