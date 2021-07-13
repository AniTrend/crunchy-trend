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

package co.anitrend.support.crunchyroll.core.android.extensions

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import co.anitrend.support.crunchyroll.domain.episode.entities.CrunchyEpisodeFeed
import coil.Coil
import coil.load
import coil.request.CachePolicy
import coil.request.Disposable
import coil.request.ImageRequest
import coil.size.Scale
import coil.target.Target
import coil.transform.GrayscaleTransformation
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation

fun AppCompatImageView.setImageUrl(url: String?) = url?.let {
    load(url) {
        scale(Scale.FIT)
        diskCachePolicy(CachePolicy.ENABLED)
    }
}

fun AppCompatImageView.setImageUrl(feed: CrunchyEpisodeFeed?) = feed?.let {
    load(it.episodeThumbnail) {
        scale(Scale.FILL)
        val comparisonTime = if (feed.isPremiumEnabled)
            it.premiumAvailableTime
        else it.freeAvailableTime

        val currentTime = System.currentTimeMillis()
        if (comparisonTime > currentTime)
            transformations(
                //BlurTransformation(context),
                GrayscaleTransformation()
            )
        diskCachePolicy(CachePolicy.ENABLED)
    }
}


/**
 * Draws an image onto the image view
 *
 * @param resource resource to load of any sub type of [Any]
 * @param transformations Optional image transformations, providing this with an empty list will
 * bypass the default [RoundedCornersTransformation] on bottom corners.
 *
 * @return A [Disposable] contract
 */
fun <T> Target.using(
    resource: T?,
    context: Context,
    transformations: List<Transformation> = emptyList()
): Disposable {
    val requestBuilder = ImageRequest.Builder(context)

    if (transformations.isNotEmpty())
        requestBuilder.transformations(transformations)

    val request = requestBuilder
        .data(resource)
        .target(this)
        .build()

    return Coil.imageLoader(context).enqueue(request)
}