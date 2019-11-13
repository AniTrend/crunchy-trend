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

package co.anitrend.support.crunchyroll.data.collection.model.contract

import co.anitrend.support.crunchyroll.data.arch.enums.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyImageSet

interface ICrunchyCollection {

    val availability_notes: String
    val media_type: CrunchyMediaType
    val series_id: Int
    val collection_id: Int
    val complete: Boolean
    val name: String
    val description: String
    val landscape_image: CrunchyImageSet?
    val portrait_image: CrunchyImageSet?
    val season: String
    val created: String


    fun isSpecial() =
        season == "0"
}