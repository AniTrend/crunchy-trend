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

package co.anitrend.support.crunchyroll.feature.news.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.extra
import co.anitrend.arch.extension.startNewActivity
import co.anitrend.arch.ui.util.SupportStateLayoutConfiguration
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews
import co.anitrend.support.crunchyroll.feature.news.R
import co.anitrend.support.crunchyroll.feature.news.koin.injectFeatureModules
import co.anitrend.support.crunchyroll.feature.news.presenter.NewsPresenter
import io.noties.markwon.Markwon
import kotlinx.android.synthetic.main.news_screen.*
import kotlinx.android.synthetic.main.news_screen_content.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.koin.android.ext.android.inject

class NewsScreen : CrunchyActivity<CrunchyNews, CrunchyCorePresenter>() {

    private val markwon by inject<Markwon>()
    private val stateConfiguration
            by inject<SupportStateLayoutConfiguration>()

    override val supportPresenter by inject<NewsPresenter>()

    private val payload
            by extra<NavigationTargets.News.Payload>(
                NavigationTargets.News.PAYLOAD
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_screen)
        setSupportActionBar(bottomAppBar)
        stateLayout.stateConfiguration = stateConfiguration
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {
        BetterLinkMovementMethod.linkify(
            Linkify.ALL,
            this
        ).setOnLinkClickListener { _, url ->
            runCatching {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = url.toUri()
                startActivity(intent)
            }.exceptionOrNull()?.printStackTrace()
            true
        }
        floatingShortcutButton.setOnClickListener {
            val shareCompat = ShareCompat.IntentBuilder
                .from(this)
                .setType("text/plain")
                .setSubject(payload?.title)
                .setHtmlText(payload?.description)
                .createChooserIntent()
            runCatching {
                startActivity(shareCompat)
            }.exceptionOrNull()?.printStackTrace()
        }
        injectFeatureModules()
        onUpdateUserInterface()
    }

    override fun onUpdateUserInterface() {
        launch {
            val html = supportPresenter.createCustomHtml(payload)
            stateLayout?.setNetworkState(NetworkState.Success)
            markwon.setMarkdown(mediaNewsContent, html)
        }
    }
}