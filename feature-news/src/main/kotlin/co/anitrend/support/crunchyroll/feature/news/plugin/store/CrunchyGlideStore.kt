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

package co.anitrend.support.crunchyroll.feature.news.plugin.store

import android.content.Context
import android.graphics.drawable.Drawable
import co.anitrend.support.crunchyroll.feature.news.R
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin

internal class CrunchyGlideStore private constructor(
    private val context: Context
) : GlideImagesPlugin.GlideStore {

    override fun cancel(target: Target<*>) {
        Glide.with(context).clear(target)
    }

    override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
        return Glide.with(context).load(drawable.destination)
            .transform(
                CenterCrop(),
                RoundedCorners(
                    context.resources
                        .getDimensionPixelSize(
                            R.dimen.md_margin
                        )
                )
            ).placeholder(R.drawable.ic_launcher_foreground)
    }

    companion object {
        fun create(context: Context) = CrunchyGlideStore(context)
    }
}