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

package co.anitrend.support.crunchyroll.data.arch

import androidx.annotation.StringDef


@StringDef(
    ResponseStatusContract.BAD_REQUEST,
    ResponseStatusContract.BAD_SESSION,
    ResponseStatusContract.OBJECT_NOT_FOUND,
    ResponseStatusContract.OK
)
@Target(AnnotationTarget.TYPEALIAS)
annotation class ResponseStatusContract {
    companion object {
        const val BAD_REQUEST = "bad_request"
        const val BAD_SESSION = "bad_session"
        const val OBJECT_NOT_FOUND = "object_not_found"
        const val OK = "ok"
    }
}

@ResponseStatusContract
typealias ResponseStatus = String

@StringDef(AccessTypeContract.PREMIUM)
@Target(AnnotationTarget.TYPEALIAS)
annotation class AccessTypeContract {
    companion object {
        const val PREMIUM = "premium"
    }
}

@AccessTypeContract
typealias AccessType = String


@StringDef(
    CrunchyMediaTypeContract.ANIME,
    CrunchyMediaTypeContract.DRAMA
)
@Target(AnnotationTarget.TYPEALIAS)
annotation class CrunchyMediaTypeContract {
    companion object {
        const val ANIME = "anime"
        const val DRAMA = "drama"
    }
}

@CrunchyMediaTypeContract
typealias CrunchyMediaType = String


@StringDef(
    StreamQualityContract.ADAPTIVE,
    StreamQualityContract.LOW,
    StreamQualityContract.MEDIUM,
    StreamQualityContract.HIGH,
    StreamQualityContract.ULTRA
)
@Target(AnnotationTarget.TYPEALIAS)
annotation class StreamQualityContract {
    companion object {
        const val ADAPTIVE = "adaptive"
        const val LOW = "low"
        const val MEDIUM = "mid"
        const val HIGH = "high"
        const val ULTRA = "ultra"
    }
}

@StreamQualityContract
typealias StreamQuality = String

@StringDef(
    MediaFieldsContract.MOST_LIKELY_MEDIA,
    MediaFieldsContract.MEDIA,
    MediaFieldsContract.MEDIA_NAME,
    MediaFieldsContract.MEDIA_DESCRIPTION,
    MediaFieldsContract.MEDIA_EPISODE_NUMBER,
    MediaFieldsContract.MEDIA_DURATION,
    MediaFieldsContract.MEDIA_PLAYHEAD,
    MediaFieldsContract.MEDIA_SCREENSHOT_IMAGE,
    MediaFieldsContract.MEDIA_MEDIA_ID,
    MediaFieldsContract.MEDIA_SERIES_ID,
    MediaFieldsContract.MEDIA_SERIES_NAME,
    MediaFieldsContract.MEDIA_COLLECTION_ID,
    MediaFieldsContract.MEDIA_URL,
    MediaFieldsContract.MEDIA_STREAM_DATA
)
annotation class MediaFieldsContract {
    companion object {

        const val MOST_LIKELY_MEDIA = "most_likely_media"
        const val MEDIA = "media"
        const val MEDIA_NAME = "media.name"
        const val MEDIA_DESCRIPTION = "media.description"
        const val MEDIA_EPISODE_NUMBER = "media.episode_number"
        const val MEDIA_DURATION = "media.duration"
        const val MEDIA_PLAYHEAD = "media.playhead"
        const val MEDIA_SCREENSHOT_IMAGE = "media.screenshot_image"
        const val MEDIA_MEDIA_ID = "media.media_id"
        const val MEDIA_SERIES_ID = "media.series_id"
        const val MEDIA_SERIES_NAME = "media.series_name"
        const val MEDIA_COLLECTION_ID = "media.collection_id"
        const val MEDIA_URL = "media.url"
        const val MEDIA_STREAM_DATA = "media.stream_data"

        val ALL = listOf(
            MOST_LIKELY_MEDIA,
            MEDIA,
            MEDIA_NAME,
            MEDIA_DESCRIPTION,
            MEDIA_EPISODE_NUMBER,
            MEDIA_DURATION,
            MEDIA_PLAYHEAD,
            MEDIA_SCREENSHOT_IMAGE,
            MEDIA_MEDIA_ID,
            MEDIA_SERIES_ID,
            MEDIA_SERIES_NAME,
            MEDIA_COLLECTION_ID,
            MEDIA_URL,
            MEDIA_STREAM_DATA
        )

        val mediaFields = listOf(
            MOST_LIKELY_MEDIA,
            MEDIA,
            MEDIA_NAME,
            MEDIA_DESCRIPTION,
            MEDIA_EPISODE_NUMBER,
            MEDIA_DURATION,
            MEDIA_PLAYHEAD,
            MEDIA_SCREENSHOT_IMAGE,
            MEDIA_MEDIA_ID,
            MEDIA_SERIES_ID,
            MEDIA_SERIES_NAME,
            MEDIA_COLLECTION_ID,
            MEDIA_URL
        ).joinToString(separator = ",")

        val streamFields = listOf(
            MEDIA_PLAYHEAD,
            MEDIA_STREAM_DATA
        ).joinToString(separator = ",")
    }
}