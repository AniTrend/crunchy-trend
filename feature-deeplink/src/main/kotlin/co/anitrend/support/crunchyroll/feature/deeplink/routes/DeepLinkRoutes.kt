/*
 *    Copyright 2021 AniTrend
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

package co.anitrend.support.crunchyroll.feature.deeplink.routes

import android.content.Intent
import androidx.annotation.VisibleForTesting
import co.anitrend.arch.extension.ext.capitalizeWords
import co.anitrend.arch.extension.ext.empty
import co.anitrend.support.crunchyroll.navigation.*
import co.anitrend.support.crunchyroll.navigation.extensions.forActivityIntent
import com.hellofresh.deeplink.Action
import com.hellofresh.deeplink.BaseRoute
import com.hellofresh.deeplink.DeepLinkUri
import com.hellofresh.deeplink.Environment
import timber.log.Timber

object MainRoute : BaseRoute<Intent?>(
    "",
    "videos",
    "videos/*/:category",
    "anime-feature",
    "anime-news",
    "news"
) {

    @VisibleForTesting
    fun extractNavDestination(
        segments: List<String>,
        params: Map<String, String>
    ): Main.Payload.Nav {
        return if (params.isEmpty())
            Main.Payload.Nav.CATALOGUE
        else when (params["category"]) {
            "popular" -> Main.Payload.Nav.CATALOGUE
            "updated" -> Main.Payload.Nav.LATEST
            else -> when (segments.last()) {
                "videos" -> Main.Payload.Nav.LATEST
                else -> Main.Payload.Nav.NEWS
            }
        }
    }

    override fun run(uri: DeepLinkUri, params: Map<String, String>, env: Environment): Intent? {
        val intent = Main.forActivityIntent(env.context)
        val nav = extractNavDestination(uri.pathSegments(), params)
        intent?.putExtra(
            Main.extraKey,
            Main.Payload(nav)
        )
        return intent
    }
}

object SeriesRoute : BaseRoute<Intent?>(":series_slug") {
    @VisibleForTesting
    fun extractIdFromSlug(slug: String): Long {
        val segments = slug.split('-')
        return segments.last().toLong()
    }

    override fun run(uri: DeepLinkUri, params: Map<String, String>, env: Environment): Intent? {
        val slug = requireNotNull(params["series_slug"])

        return if (slug.startsWith("media")) {
            val intent = MediaPlayer.forActivityIntent(env.context)
            val payload = MediaPlayer.Payload(
                mediaId = extractIdFromSlug(slug),
                collectionName = String.empty(),
                collectionThumbnail = String.empty(),
                episodeTitle = String.empty(),
                episodeThumbnail = String.empty()
            )
            intent?.putExtra(MediaPlayer.extraKey, payload)
        } else {
            val intent = Series.forActivityIntent(env.context)
            val payload = Series.Payload(
                seriesId = extractIdFromSlug(slug)
            )
            intent?.putExtra(Series.extraKey, payload)
        }
    }
}

object PlayerRoute : BaseRoute<Intent?>(":series_slug/:episode_slug") {
    @VisibleForTesting
    fun extractIdFromSlug(episodeSlug: String): Long {
        val segments = episodeSlug.split('-')
        return segments.last().toLong()
    }

    @VisibleForTesting
    fun extractEpisodeTitle(episodeSlug: String): String {
        val segments = episodeSlug.split('-')
        val size = segments.size
        val prefix = segments.take(2)
            .capitalizeWords()
            .joinToString(" ")
        val description = segments.subList(2, size - 1)
            .capitalizeWords()
            .joinToString(" ")
        return "$prefix - $description"
    }

    @VisibleForTesting
    fun extractSeriesTitle(seriesSlug: String?): String? {
        val segments = seriesSlug?.split('-')
        return segments?.joinToString(" ")
    }

    override fun run(uri: DeepLinkUri, params: Map<String, String>, env: Environment): Intent? {
        val seriesSlug = requireNotNull(params["series_slug"])
        val episodeSlug = requireNotNull(params["episode_slug"])

        val intent = MediaPlayer.forActivityIntent(env.context)

        val payload = MediaPlayer.Payload(
            mediaId = extractIdFromSlug(episodeSlug),
            collectionName = extractSeriesTitle(seriesSlug),
            collectionThumbnail = String.empty(),
            episodeTitle = extractEpisodeTitle(episodeSlug),
            episodeThumbnail = String.empty()
        )
        intent?.putExtra(MediaPlayer.extraKey, payload)

        return intent
    }
}

object FallbackRoute : Action<Intent?> {
    override fun run(uri: DeepLinkUri, params: Map<String, String>, env: Environment): Intent? {
        val entries = params.entries.joinToString()
        Timber.w("Registered routers failed to match | uri: $uri | params: $entries")
        return Splash.forActivityIntent(env.context)
    }
}