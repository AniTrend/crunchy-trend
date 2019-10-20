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

package co.anitrend.support.crunchyroll.koin

import android.graphics.drawable.Drawable
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.auth.presenter.AuthPresenter
import co.anitrend.support.crunchyroll.listings.presenter.ListingPresenter
import co.anitrend.support.crunchyroll.plugin.CrunchyTagPlugin
import co.anitrend.support.crunchyroll.stream.presenter.StreamPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.Target
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.AsyncDrawable
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

private val appModule = module {
    single {
        Markwon.builder(androidApplication())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(CrunchyTagPlugin.create())
            .usePlugin(GlideImagesPlugin.create(object : GlideImagesPlugin.GlideStore {
                override fun cancel(target: Target<*>) {
                    Glide.with(androidApplication()).clear(target)
                }

                override fun load(drawable: AsyncDrawable): RequestBuilder<Drawable> {
                    return Glide.with(androidApplication()).load(drawable.destination)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(androidApplication().resources.getDimensionPixelSize(R.dimen.md_margin))
                        ).placeholder(R.drawable.ic_crunchyroll)
                }
            }))
            .build()
    }
}

private val presenterModule = module {
    factory {
        AuthPresenter(
            context = androidApplication(),
            settings = get()
        )
    }
    factory {
        ListingPresenter(
            context = androidApplication(),
            settings = get()
        )
    }
    factory {
        StreamPresenter(
            context = androidApplication(),
            settings = get()
        )
    }
}

val appModules = listOf(appModule, presenterModule)