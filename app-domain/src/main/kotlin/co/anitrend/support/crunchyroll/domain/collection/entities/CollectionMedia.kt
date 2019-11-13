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

package co.anitrend.support.crunchyroll.domain.collection.entities

data class CollectionMedia(
    val collectionId: Long,
    val seriesId: Long,
    val name: String,
    val description: String,
    val mediaType: String,
    val season: String,
    val complete: Boolean,
    val landscapeImage: String?,
    val portraitImage: String?,
    val availabilityNotes: String?,
    val created: Long
)