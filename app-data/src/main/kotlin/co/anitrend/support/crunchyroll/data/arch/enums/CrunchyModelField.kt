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

internal enum class CrunchyModelField(
    override val attribute: String
) : ICrunchyEnumAttribute {
    IMAGE_FULL_URL("image.full_url"),
    IMAGE_FWIDE_URL("image.fwide_url"),
    IMAGE_FWIDESTAR_URL("image.fwidestar_url"),
    IMAGE_HEIGHT("image.height"),
    IMAGE_LARGE_URL("image.large_url"),
    IMAGE_MEDIUM_URL("image.medium_url"),
    IMAGE_SMALL_URL("image.small_url"),
    IMAGE_THUMB_URL("image.thumb_url"),
    IMAGE_WIDE_URL("image.wide_url"),
    IMAGE_WIDESTAR_URL("image.widestar_url"),
    IMAGE_WIDTH("image.width"),
    COLLECTION("collection"),
    MEDIA("media"),
    MEDIA_AVAILABILITY_NOTES("media.availability_notes"),
    MEDIA_AVAILABLE("media.available"),
    MEDIA_AVAILABLE_TIME("media.available_time"),
    MEDIA_BIF_URL("media.bif_url"),
    MEDIA_CLASS("media.class"),
    MEDIA_CLIP("media.clip"),
    MEDIA_COLLECTION_ID("media.collection_id"),
    MEDIA_COLLECTION_NAME("media.collection_name"),
    MEDIA_CREATED("media.created"),
    MEDIA_DESCRIPTION("media.description"),
    MEDIA_DURATION("media.duration"),
    MEDIA_EPISODE_NUMBER("media.episode_number"),
    MEDIA_FREE_AVAILABLE("media.free_available"),
    MEDIA_FREE_AVAILABLE_TIME("media.free_available_time"),
    MEDIA_FREE_UNAVAILABLE_TIME("media.free_unavailable_time"),
    MEDIA_MEDIA_ID("media.media_id"),
    MEDIA_MEDIA_TYPE("media.media_type"),
    MEDIA_NAME("media.name"),
    MEDIA_PLAYHEAD("media.playhead"),
    MEDIA_PREMIUM_AVAILABLE("media.premium_available"),
    MEDIA_PREMIUM_AVAILABLE_TIME("media.premium_available_time"),
    MEDIA_PREMIUM_ONLY("media.premium_only"),
    MEDIA_PREMIUM_UNAVAILABLE_TIME("media.premium_unavailable_time"),
    MEDIA_SCREENSHOT_IMAGE("media.screenshot_image"),
    MEDIA_SERIES_ID("media.series_id"),
    MEDIA_SERIES_NAME("media.series_name"),
    MEDIA_STREAM_DATA("media.stream_data"),
    MEDIA_ETP_GUID("media.etp_guid"),
    MEDIA_UNAVAILABLE_TIME("media.unavailable_time"),
    MEDIA_URL("media.url"),
    LAST_WATCHED_MEDIA("last_watched_media"),
    LAST_WATCHED_MEDIA_PLAYHEAD("last_watched_media_playhead"),
    MOST_LIKELY_MEDIA("most_likely_media"),
    MOST_LIKELY_MEDIA_PLAYHEAD("most_likely_media_playhead"),
    ORDERING("ordering"),
    PLAYHEAD("playhead"),
    QUEUE_ENTRY_ID("queue_entry_id"),
    TIME_STAMP("timestamp"),
    SERIES("series"),
    SERIES_CLASS("series.class"),
    SERIES_COLLECTION_COUNT("series.collection_count"),
    SERIES_DESCRIPTION("series.description"),
    SERIES_GENRES("series.genres"),
    SERIES_IN_QUEUE("series.in_queue"),
    SERIES_LANDSCAPE_IMAGE("series.landscape_image"),
    SERIES_MEDIA_COUNT("series.media_count"),
    SERIES_MEDIA_TYPE("series.media_type"),
    SERIES_NAME("series.name"),
    SERIES_PORTRAIT_IMAGE("series.portrait_image"),
    SERIES_PUBLISHER_NAME("series.publisher_name"),
    SERIES_RATING("series.rating"),
    SERIES_SERIES_ID("series.series_id"),
    SERIES_URL("series.url"),
    SERIES_YEAR("series.year");

    companion object {
        val mediaFields = listOf(
            IMAGE_FULL_URL.attribute,
            MEDIA_COLLECTION_NAME.attribute,
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
            MEDIA_URL.attribute,
            MEDIA_ETP_GUID.attribute,
            MEDIA_AVAILABLE_TIME.attribute,
            MEDIA_PREMIUM_AVAILABLE_TIME.attribute,
            MEDIA_FREE_AVAILABLE_TIME.attribute,
            MEDIA_AVAILABILITY_NOTES.attribute
        ).joinToString(separator = ",")

        val seriesFields = listOf(
            IMAGE_FULL_URL.attribute,
            SERIES.attribute,
            SERIES_COLLECTION_COUNT.attribute,
            SERIES_DESCRIPTION.attribute,
            SERIES_GENRES.attribute,
            SERIES_IN_QUEUE.attribute,
            SERIES_LANDSCAPE_IMAGE.attribute,
            SERIES_MEDIA_COUNT.attribute,
            SERIES_MEDIA_TYPE.attribute,
            SERIES_NAME.attribute,
            SERIES_PORTRAIT_IMAGE.attribute,
            SERIES_PUBLISHER_NAME.attribute,
            SERIES_RATING.attribute,
            SERIES_SERIES_ID.attribute,
            SERIES_URL.attribute,
            SERIES_YEAR.attribute
        ).joinToString(separator = ",")

        val streamFields = listOf(
            MEDIA_PLAYHEAD.attribute,
            MEDIA_STREAM_DATA.attribute
        ).joinToString(separator = ",")

        val collectionFields = listOf(
            COLLECTION.attribute
        ).joinToString(separator = ",")

        val recentlyWatchedFields = listOf(
            PLAYHEAD.attribute,
            TIME_STAMP.attribute,
            IMAGE_FULL_URL.attribute,
            SERIES.attribute,
            SERIES_COLLECTION_COUNT.attribute,
            SERIES_DESCRIPTION.attribute,
            SERIES_GENRES.attribute,
            SERIES_IN_QUEUE.attribute,
            SERIES_LANDSCAPE_IMAGE.attribute,
            SERIES_MEDIA_COUNT.attribute,
            SERIES_MEDIA_TYPE.attribute,
            SERIES_NAME.attribute,
            SERIES_PORTRAIT_IMAGE.attribute,
            SERIES_PUBLISHER_NAME.attribute,
            SERIES_RATING.attribute,
            SERIES_SERIES_ID.attribute,
            SERIES_URL.attribute,
            SERIES_YEAR.attribute,
            MEDIA_COLLECTION_NAME.attribute,
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
            MEDIA_URL.attribute,
            MEDIA_ETP_GUID.attribute,
            MEDIA_AVAILABLE_TIME.attribute,
            MEDIA_PREMIUM_AVAILABLE_TIME.attribute,
            MEDIA_FREE_AVAILABLE_TIME.attribute,
            MEDIA_AVAILABILITY_NOTES.attribute,
            COLLECTION.attribute
        ).joinToString(separator = ",")

        val queueFields = listOf(
            QUEUE_ENTRY_ID.attribute,
            ORDERING.attribute,
            MOST_LIKELY_MEDIA.attribute,
            MOST_LIKELY_MEDIA_PLAYHEAD.attribute,
            LAST_WATCHED_MEDIA.attribute,
            LAST_WATCHED_MEDIA_PLAYHEAD.attribute,
            PLAYHEAD.attribute,
            IMAGE_FULL_URL.attribute,
            SERIES.attribute,
            SERIES_COLLECTION_COUNT.attribute,
            SERIES_DESCRIPTION.attribute,
            SERIES_GENRES.attribute,
            SERIES_IN_QUEUE.attribute,
            SERIES_LANDSCAPE_IMAGE.attribute,
            SERIES_MEDIA_COUNT.attribute,
            SERIES_MEDIA_TYPE.attribute,
            SERIES_NAME.attribute,
            SERIES_PORTRAIT_IMAGE.attribute,
            SERIES_PUBLISHER_NAME.attribute,
            SERIES_RATING.attribute,
            SERIES_SERIES_ID.attribute,
            SERIES_URL.attribute,
            SERIES_YEAR.attribute,
            MEDIA_COLLECTION_NAME.attribute,
            MOST_LIKELY_MEDIA.attribute,
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
            MEDIA_URL.attribute,
            MEDIA_ETP_GUID.attribute,
            MEDIA_AVAILABLE_TIME.attribute,
            MEDIA_PREMIUM_AVAILABLE_TIME.attribute,
            MEDIA_FREE_AVAILABLE_TIME.attribute,
            MEDIA_AVAILABILITY_NOTES.attribute
        ).joinToString(separator = ",")
    }
}