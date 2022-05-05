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

import co.anitrend.support.crunchyroll.data.arch.mapper.DefaultMapper
import co.anitrend.support.crunchyroll.data.batch.entity.CrunchyBatchEntity
import co.anitrend.support.crunchyroll.data.catalog.datasource.local.CrunchyCatalogDao
import co.anitrend.support.crunchyroll.data.catalog.entity.CrunchyCatalogEntity
import co.anitrend.support.crunchyroll.data.catalog.extension.generateHashCode
import co.anitrend.support.crunchyroll.data.series.converters.SeriesModelConverter
import co.anitrend.support.crunchyroll.data.series.entity.CrunchySeriesEntity
import co.anitrend.support.crunchyroll.data.series.mapper.SeriesResponseMapper
import co.anitrend.support.crunchyroll.data.series.model.CrunchySeriesModel
import co.anitrend.support.crunchyroll.domain.catalog.enums.CrunchySeriesCatalogFilter

internal class CatalogResponseMapper(
    private val dao: CrunchyCatalogDao,
    private val seriesResponseMapper: SeriesResponseMapper
) : DefaultMapper<List<CrunchyBatchEntity<CrunchySeriesModel>>, List<CrunchyCatalogEntity>>() {

    /**
     * Save [data] into your desired local source
     */
    override suspend fun persist(data: List<CrunchyCatalogEntity>) {
        dao.upsert(data)
    }

    private suspend fun onResponseMapFromForSeries(source: CrunchyBatchEntity<CrunchySeriesModel>): List<CrunchySeriesEntity> {
        val seriesEntities = source.data.let(SeriesModelConverter::convertFrom)
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
    override suspend fun onResponseMapFrom(source: List<CrunchyBatchEntity<CrunchySeriesModel>>): List<CrunchyCatalogEntity> {
        val catalogs = CrunchySeriesCatalogFilter.values()
        source.mapIndexed { index, entity ->
            val seriesEntities = onResponseMapFromForSeries(entity)
            val catalogFilter = catalogs[index]

            val catalogItems = seriesEntities.mapIndexed { seriesIndex, series ->
                val rank = (seriesIndex + 1) * (index + 1)
                CrunchyCatalogEntity(
                    seriesId = series.id,
                    seriesRank = rank,
                    catalogFilter = catalogFilter,
                    catalogId = catalogFilter.generateHashCode(series.id)
                )
            }
            onResponseDatabaseInsert(catalogItems)
        }
        return emptyList()
    }
}