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

import androidx.databinding.BindingAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.domain.episode.entities.CrunchyEpisodeFeed
import coil.api.load
import coil.request.CachePolicy
import coil.size.Scale
import coil.transform.GrayscaleTransformation


@BindingAdapter("imageUrl")
fun AppCompatImageView.setImageUrl(url: String?) = url?.also {
    if (url.isNullOrBlank())
        load(R.drawable.ic_launcher_foreground) {
            if (context is LifecycleOwner)
                lifecycle(context as LifecycleOwner)
        }
    else {
        load(url) {
            scale(Scale.FIT)
            diskCachePolicy(CachePolicy.ENABLED)
            if (context is LifecycleOwner)
                lifecycle(context as LifecycleOwner)
        }
    }
}

@BindingAdapter("imageUrl")
fun AppCompatImageView.setImageUrl(feed: CrunchyEpisodeFeed?) = feed?.also {
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
        if (context is LifecycleOwner)
            lifecycle(context as LifecycleOwner)
    }
}