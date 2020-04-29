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

package co.anitrend.support.crunchyroll.core.helper

import android.content.Context
import co.anitrend.support.crunchyroll.core.settings.common.cache.ICacheSettings
import timber.log.Timber
import java.io.File
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

object StorageHelper {

    private val moduleTag = StorageHelper::class.java.simpleName
    private const val logsName = "logs"
    private const val imageCacheName = "coil_image_cache"
    private const val videoCacheName = "exo_video_cache"
    private const val videoOfflineCacheName = "exo_video_offline_cache"

    private fun cacheDirectory(context: Context) =
        context.externalCacheDir ?: context.cacheDir


    fun getLogsCache(context: Context): File {
        val cache = cacheDirectory(context)
        val logs = File(cache, logsName)
        if (!logs.exists()) logs.mkdirs()
        Timber.tag(moduleTag).v(
            "Directory that will be used for logs: ${logs.canonicalPath}"
        )
        return logs
    }

    fun getImageCache(context: Context): File {
        val cache = cacheDirectory(context)
        val imageCache = File(cache, imageCacheName)
        if (!imageCache.exists()) imageCache.mkdirs()
        Timber.tag(moduleTag).v(
            "Cache that will be used for images: ${imageCache.canonicalPath}"
        )
        return imageCache
    }

    fun getVideoCache(context: Context): File {
        val cache = cacheDirectory(context)
        val videoCache = File(cache, videoCacheName)
        if (!videoCache.exists()) videoCache.mkdirs()
        Timber.tag(moduleTag).v(
            "Cache that will be used for videos: ${videoCache.canonicalPath}"
        )
        return videoCache
    }

    fun getVideoOfflineCache(context: Context): File {
        val cache = cacheDirectory(context)
        val videoOfflineCache = File(cache, videoOfflineCacheName)
        if (!videoOfflineCache.exists()) videoOfflineCache.mkdirs()
        Timber.tag(moduleTag).v(
            "Cache that will be used for offline videos: ${videoOfflineCache.canonicalPath}"
        )
        return videoOfflineCache
    }

    fun getFreeSpace(context: Context): Long {
        val cache = cacheDirectory(context)
        return cache.freeSpace
    }

    fun getStorageUsageLimit(context: Context, settings: ICacheSettings): Long {
        val freeSpace = getFreeSpace(context)
        val ratio = settings.usageRatio
        val limit = (freeSpace * ratio).toLong()
        Timber.tag(moduleTag).v(
            "Storage usage limit -> ratio: $ratio | limit: ${limit.toHumanReadableByteValue()}"
        )
        return limit
    }

    fun Float.toHumanReadableByteValue(si: Boolean = false): String {
        val bytes = this
        val unit = if (si) 1000 else 1024
        if (bytes < unit) return "$bytes B"
        val exp =
            (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
        val pre =
            (if (si) "kMGTPE" else "KMGTPE")[exp - 1].toString() + if (si) "" else "i"
        return String.format(
            Locale.getDefault(), "%.1f %sB",
            bytes / unit.toDouble().pow(exp.toDouble()), pre
        )
    }

    fun Long.toHumanReadableByteValue(si: Boolean = false): String {
        val bytes = this
        val unit = if (si) 1000 else 1024
        if (bytes < unit) return "$bytes B"
        val exp =
            (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
        val pre =
            (if (si) "kMGTPE" else "KMGTPE")[exp - 1].toString() + if (si) "" else "i"
        return String.format(
            Locale.getDefault(), "%.1f %sB",
            bytes / unit.toDouble().pow(exp.toDouble()), pre
        )
    }
}