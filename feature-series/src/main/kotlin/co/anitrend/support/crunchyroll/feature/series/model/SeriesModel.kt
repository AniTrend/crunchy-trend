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

package co.anitrend.support.crunchyroll.feature.series.model

import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries

data class SeriesModel(
    val name: String,
    val landscapeImage: String?,
    val portraitImage: String?,
    val description: String,
    val queued: Boolean,
    val rating: Int,
    val mediaCount: Int,
    val collectionCount: Int,
    val publisher: String,
    val year: Int,
    val genres: List<String>
) {
    companion object {
        fun fromCrunchySeries(entity: CrunchySeries): SeriesModel {
            return SeriesModel(
                name = entity.name,
                landscapeImage = entity.landscapeImage,
                portraitImage = entity.portraitImage,
                description = entity.description,
                queued = entity.queued,
                rating = entity.rating,
                mediaCount = entity.mediaCount,
                collectionCount = entity.collectionCount,
                publisher = entity.publisher,
                year = entity.year,
                genres = entity.genres.map {
                    it.capitalize()
                }
            )
        }
    }
}