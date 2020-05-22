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

package co.anitrend.support.crunchyroll.data.tracker.model

import co.anitrend.support.crunchyroll.data.media.model.CrunchyMediaModel
import co.anitrend.support.crunchyroll.data.series.model.CrunchySeriesModel

internal data class CrunchyQueueModel(
    val queue_entry_id: Long,
    val ordering: Int,
    val series: CrunchySeriesModel,
    val playhead: Short,
    val last_watched_media: CrunchyMediaModel,
    val last_watched_media_playhead: Short,
    val most_likely_media: CrunchyMediaModel,
    val most_likely_media_playhead: Short
)