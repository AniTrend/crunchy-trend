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

package co.anitrend.support.crunchyroll.data.arch.enums

import co.anitrend.support.crunchyroll.data.arch.enums.contract.ICrunchyEnumAttribute

enum class CrunchyMediaField(
    override val attribute: String
) : ICrunchyEnumAttribute {
    MOST_LIKELY_MEDIA("most_likely_media"),
    MEDIA("media"),
    MEDIA_NAME("media.name"),
    MEDIA_DESCRIPTION("media.description"),
    MEDIA_EPISODE_NUMBER("media.episode_number"),
    MEDIA_DURATION("media.duration"),
    MEDIA_PLAYHEAD("media.playhead"),
    MEDIA_SCREENSHOT_IMAGE("media.screenshot_image"),
    MEDIA_MEDIA_ID("media.media_id"),
    MEDIA_SERIES_ID("media.series_id"),
    MEDIA_SERIES_NAME("media.series_name"),
    MEDIA_COLLECTION_ID("media.collection_id"),
    MEDIA_URL("media.url"),
    MEDIA_STREAM_DATA("media.stream_data");

    companion object {
        val mediaFields = listOf(
            MOST_LIKELY_MEDIA.attribute,
            MEDIA.attribute,
            MEDIA_NAME.attribute,
            MEDIA_DESCRIPTION.attribute,
            MEDIA_EPISODE_NUMBER.attribute,
            MEDIA_DURATION.attribute,
            MEDIA_PLAYHEAD.attribute,
            MEDIA_SCREENSHOT_IMAGE.attribute,
            MEDIA_MEDIA_ID.attribute,
            MEDIA_SERIES_ID.attribute,
            MEDIA_SERIES_NAME.attribute,
            MEDIA_COLLECTION_ID.attribute,
            MEDIA_URL.attribute
        ).joinToString(separator = ",")

        val streamFields = listOf(
            MEDIA_PLAYHEAD.attribute,
            MEDIA_STREAM_DATA.attribute
        ).joinToString(separator = ",")
    }
}