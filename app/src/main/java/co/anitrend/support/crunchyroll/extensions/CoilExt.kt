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

package co.anitrend.support.crunchyroll.extensions

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import co.anitrend.support.crunchyroll.data.model.rss.MediaThumbnail
import coil.api.load
import coil.request.CachePolicy
import coil.size.Scale

@BindingAdapter("imageUrl")
fun AppCompatImageView.setImageUrl(url: String?) = url?.also {
    load(url) {
        scale(Scale.FIT)
        diskCachePolicy(CachePolicy.ENABLED)
    }
}

@BindingAdapter("imageUrl")
fun AppCompatImageView.setImageUrl(thumbnails: List<MediaThumbnail>?) {
    setImageUrl(thumbnails.highestQuality()?.url)
}

fun List<MediaThumbnail>?.highestQuality() = this?.maxBy { it.width }