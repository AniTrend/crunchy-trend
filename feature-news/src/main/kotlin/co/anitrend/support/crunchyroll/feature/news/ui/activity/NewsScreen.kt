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
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.extension.ext.extra
import co.anitrend.support.crunchyroll.core.extensions.stackTrace
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.feature.news.R
import co.anitrend.support.crunchyroll.feature.news.databinding.NewsScreenBinding
import co.anitrend.support.crunchyroll.feature.news.presenter.NewsPresenter
import co.anitrend.support.crunchyroll.navigation.News
import io.noties.markwon.Markwon
import kotlinx.coroutines.launch
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class NewsScreen : CrunchyActivity<NewsScreenBinding>() {

    private val payload by extra<News.Payload>(News.extraKey)

    private val presenter by inject<NewsPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NewsScreenBinding.inflate(layoutInflater)
        setContentView(requireBinding().root)
        setSupportActionBar(requireBinding().bottomAppBar)
        onUpdateUserInterface()
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {
        BetterLinkMovementMethod.linkify(Linkify.ALL, this)
            .setOnLinkClickListener { view, url ->
                runCatching {
                    presenter.handleViewIntent(view, url, this)
                }.stackTrace()
                true
            }
        requireBinding().floatingShortcutButton
            .setOnClickListener {
                val shareCompat = payload?.let {
                    presenter.createShareContent(it, this)
                }?.createChooserIntent()
                runCatching {
                    startActivity(shareCompat)
                }.stackTrace()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.news_reader_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
             R.id.action_open_in_browser -> {
                 runCatching {
                     val intent = Intent(Intent.ACTION_VIEW)
                     intent.data = payload?.guid?.toUri()
                     startActivity(intent)
                 }.stackTrace()
                 true
             }
             R.id.action_open_gallery -> {
                 Toast.makeText(applicationContext, "Not yet implemented", Toast.LENGTH_SHORT).show()
                 true
             }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onUpdateUserInterface() {
        lifecycleScope.launch {
            val html = presenter.createCustomHtml(payload)
            get<Markwon>().setMarkdown(requireBinding().newsContent.mediaNewsContent, html)
        }
    }
}