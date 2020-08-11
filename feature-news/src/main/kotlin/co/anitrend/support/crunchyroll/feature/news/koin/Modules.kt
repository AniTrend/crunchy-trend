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

package co.anitrend.support.crunchyroll.feature.news.koin

import co.anitrend.support.crunchyroll.core.koin.helper.DynamicFeatureModuleHelper
import co.anitrend.support.crunchyroll.feature.news.FeatureProvider
import co.anitrend.support.crunchyroll.feature.news.plugin.CrunchyTagPlugin
import co.anitrend.support.crunchyroll.feature.news.plugin.store.CrunchyGlideStore
import co.anitrend.support.crunchyroll.feature.news.presenter.NewsPresenter
import co.anitrend.support.crunchyroll.feature.news.ui.fragment.NewsFeedContent
import co.anitrend.support.crunchyroll.feature.news.viewmodel.NewsViewModel
import co.anitrend.support.crunchyroll.navigation.News
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import org.commonmark.node.Paragraph
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber

private val coreModule = module {
    single {
        Markwon.builder(androidApplication())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(CrunchyTagPlugin.create())
            .usePlugin(
                object : AbstractMarkwonPlugin() {
                    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                        builder.on(
                            Paragraph::class.java
                        ) { visitor, _ ->
                            Timber.i("visitor for paragraph: $visitor")
                        }
                    }
                }
            )
            .usePlugin(
                GlideImagesPlugin.create(
                    CrunchyGlideStore.create(androidContext())
                )
            )
            .usePlugin(SoftBreakAddsNewLinePlugin.create())
            .build()
    }
}

private val fragmentModule = module {
    fragment {
        NewsFeedContent()
    }
}

private val presenterModule = module {
    factory {
        NewsPresenter(
            context = androidContext(),
            settings = get(),
            dispatchers = get()
        )
    }
}

private val viewModelModule = module {
    viewModel {
        NewsViewModel(
            useCase = get()
        )
    }
}

private val featureModule = module {
    factory<News.Provider> {
        FeatureProvider()
    }
}

val moduleHelper by lazy {
    DynamicFeatureModuleHelper(
        listOf(coreModule, fragmentModule, presenterModule, viewModelModule, featureModule)
    )
}