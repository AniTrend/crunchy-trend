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
import android.net.Uri
import android.os.Bundle
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.net.toUri
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.extra
import co.anitrend.support.crunchyroll.core.android.widgets.ElasticDragDismissFrameLayout
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
import kotlinx.coroutines.launch
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class NewsScreen : CrunchyActivity() {

    override val elasticLayout: ElasticDragDismissFrameLayout? = null

    private val payload
            by extra<NavigationTargets.News.Payload>(
                NavigationTargets.News.PAYLOAD
            )

    private val presenter by inject<NewsPresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.news_screen)
        setSupportActionBar(bottomAppBar)
        stateLayout.stateConfig = get()
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {
        injectFeatureModules()
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
            val shareCompat = payload?.let {
                presenter.createShareContent(it, get(), this)
            }?.createChooserIntent()
            runCatching {
                startActivity(shareCompat)
            }.exceptionOrNull()?.printStackTrace()
        }
        onUpdateUserInterface()
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     *
     * This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * [.onPrepareOptionsMenu].
     *
     *
     * The default implementation populates the menu with standard system
     * menu items.  These are placed in the [Menu.CATEGORY_SYSTEM] group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     *
     * You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     *
     * When you add items to the menu, you can implement the Activity's
     * [.onOptionsItemSelected] method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     *
     * @see .onPrepareOptionsMenu
     *
     * @see .onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.news_reader_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
             R.id.action_open_in_browser -> {
                 kotlin.runCatching {
                     val url = payload?.let {
                         presenter.buildNewsUrl(it, get())
                     }
                     val intent = Intent(Intent.ACTION_VIEW)
                     intent.data = url?.toUri()
                     startActivity(intent)
                 }.exceptionOrNull()?.printStackTrace()
                 true
             }
             R.id.action_open_gallery -> {
                 Toast.makeText(applicationContext, "Not yet implemented", Toast.LENGTH_SHORT).show()
                 true
             }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onUpdateUserInterface() {
        launch {
            val html = presenter.createCustomHtml(payload)
            stateLayout?.setNetworkState(NetworkState.Success)
            get<Markwon>().setMarkdown(mediaNewsContent, html)
        }
    }
}