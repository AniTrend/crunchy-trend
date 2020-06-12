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

package co.anitrend.support.crunchyroll.feature.news.presenter

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.arch.extension.util.contract.ISupportDateHelper
import co.anitrend.support.crunchyroll.core.extensions.stackTrace
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.feature.news.model.Poster
import co.anitrend.support.crunchyroll.feature.news.ui.activity.NewsScreen
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class NewsPresenter(
    context: Context,
    settings: CrunchySettings,
    private val dispatchers: SupportDispatchers
) : CrunchyCorePresenter(context, settings) {

    // TODO: Remove invalid characters from url
    private val regex = Regex("[,`:#!'\"]")

    private val posters = ArrayList<Poster>()

    suspend fun createCustomHtml(
        payload: NavigationTargets.News.Payload?
    ): String {
        return withContext(dispatchers.computation) {
            val template = """
                <p><h3>${payload?.title}</h3></p>
                <h5>${payload?.subTitle}</h5>
            """.trimIndent()
            val document = Jsoup.parse("$template${payload?.content}")

            document.getElementsByTag("img").forEach { tag ->
                val src = tag.attr("src")

                val width = runCatching{
                    tag.attr("width")?.toShort()
                }.stackTrace(moduleTag) ?: 0

                val height = runCatching{
                    tag.attr("height")?.toShort()
                }.stackTrace(moduleTag) ?: 0

                if (height > 100) {
                    Timber.tag(moduleTag).v("Image found -> $src | $width x $height")
                    posters.add(Poster(src, width, height))
                }
                else
                    Timber.tag(moduleTag).v("Ignoring image -> $src | $width x $height")
            }
            val html = document.html()
            html
        }
    }

    fun buildNewsUrl(
        payload: NavigationTargets.News.Payload,
        dateHelper: ISupportDateHelper
    ): String {
        val payloadContent = StringBuilder(120)

        val url = "https://www.crunchyroll.com/en-gb/anime-feature"
        payloadContent.append(url, '/')

        val date = dateHelper.convertFromUnixTimeStamp(
            unixTimeStamp = payload.publishDate ?: 0,
            outputDatePattern = "yyyy/MM/dd"
        )
        payloadContent.append(date, '/')

        val slug = payload.title.toLowerCase(
            Locale.getDefault()
        ).replace(regex, "")
            .replace(' ', '-')
        payloadContent.append(slug)

        return payloadContent.toString()
    }

    fun createShareContent(
        payload: NavigationTargets.News.Payload,
        dateHelper: ISupportDateHelper,
        newsScreen: NewsScreen
    ): ShareCompat.IntentBuilder {
        val url = buildNewsUrl(payload, dateHelper)

        val payloadContent = StringBuilder(payload.description!!)
            .append("\n\n")
            .append(url)

        return ShareCompat.IntentBuilder
            .from(newsScreen)
            .setType("text/plain")
            .setSubject(payload.title)
            .setHtmlText(payloadContent.toString())
    }

    fun handleViewIntent(view: View, url: String, context: FragmentActivity) {
        if (url.startsWith("https://img1.ak.crunchyroll")) {
            ViewCompat.setTransitionName(view, url)
            val payload = NavigationTargets.ImageViewer.Payload(url)
            NavigationTargets.ImageViewer(context, payload)
        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = url.toUri()
            context.startActivity(intent)
        }
    }
}