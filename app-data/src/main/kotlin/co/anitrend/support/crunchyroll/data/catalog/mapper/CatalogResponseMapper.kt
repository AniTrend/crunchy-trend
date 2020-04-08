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

package co.anitrend.support.crunchyroll.data.catalog.mapper

import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyMapper
import co.anitrend.support.crunchyroll.data.catalog.datasource.local.CrunchyCatalogDao
import co.anitrend.support.crunchyroll.data.catalog.entity.CrunchyCatalogEntity
import co.anitrend.support.crunchyroll.data.series.entity.CrunchySeriesEntity
import co.anitrend.support.crunchyroll.data.series.mapper.SeriesResponseMapper
import co.anitrend.support.crunchyroll.data.series.model.CrunchySeriesModel
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter

class CatalogResponseMapper(
    private val dao: CrunchyCatalogDao,
    private val seriesResponseMapper: SeriesResponseMapper,
    private val seriesCatalogFilter: CrunchySeriesCatalogFilter
) : CrunchyMapper<List<CrunchySeriesModel>, List<CrunchyCatalogEntity>>() {

    private suspend fun onResponseMapFromForSeries(source: List<CrunchySeriesModel>): List<CrunchySeriesEntity> {
        val seriesEntities = seriesResponseMapper.onResponseMapFrom(source)
        if (seriesEntities.isNotEmpty())
            seriesResponseMapper.onResponseDatabaseInsert(seriesEntities)

        return seriesEntities
    }

    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     * called in [retrofit2.Callback.onResponse] after assuring that the response was a success
     *
     * @param source the incoming data source type
     * @return Mapped object that will be consumed by [onResponseDatabaseInsert]
     */
    override suspend fun onResponseMapFrom(source: List<CrunchySeriesModel>): List<CrunchyCatalogEntity> {
        val seriesEntities = onResponseMapFromForSeries(source)
        return seriesEntities.map {
            CrunchyCatalogEntity(
                seriesId = it.id,
                catalogFilter = seriesCatalogFilter
            )
        }
    }

    /**
     * Inserts the given object into the implemented room database,
     * called in [retrofit2.Callback.onResponse]
     *
     * @param mappedData mapped object from [onResponseMapFrom] to insert into the database
     */
    override suspend fun onResponseDatabaseInsert(mappedData: List<CrunchyCatalogEntity>) {
        if (mappedData.isNotEmpty())
            dao.upsert(mappedData)
    }
}