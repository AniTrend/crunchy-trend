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

package co.anitrend.support.crunchyroll.data.media.mapper

import co.anitrend.support.crunchyroll.data.arch.mapper.DefaultMapper
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.media.datasource.local.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.media.datasource.local.transformer.CrunchyMediaEntityTransformer
import co.anitrend.support.crunchyroll.data.media.entity.CrunchyMediaEntity
import co.anitrend.support.crunchyroll.data.media.model.CrunchyMediaModel

internal class MediaResponseMapper(
    private val dao: CrunchyMediaDao
) : DefaultMapper<CrunchyContainer<List<CrunchyMediaModel>>, List<CrunchyMediaEntity>>() {

    /**
     * Save [data] into your desired local source
     */
    override suspend fun persist(data: List<CrunchyMediaEntity>) {
        dao.upsert(data)
    }

    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     *
     * @param source the incoming data source type
     * @return mapped object that will be consumed by [onResponseDatabaseInsert]
     */
    override suspend fun onResponseMapFrom(source: CrunchyContainer<List<CrunchyMediaModel>>): List<CrunchyMediaEntity> {
        return source.data?.map(
            CrunchyMediaEntityTransformer::transform
        ).orEmpty()
    }
}