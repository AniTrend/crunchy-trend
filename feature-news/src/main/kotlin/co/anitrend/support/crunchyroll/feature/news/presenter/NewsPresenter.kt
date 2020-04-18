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
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class NewsPresenter(
    context: Context,
    settings: CrunchySettings,
    private val dispatchers: SupportDispatchers
) : CrunchyCorePresenter(context, settings) {

    suspend fun createCustomHtml(
        payload: NavigationTargets.News.Payload?
    ): String {
        return withContext(dispatchers.computation) {
            val template = """
                <p><h3>${payload?.title}</h3></p>
                <h5>${payload?.subTitle}</h5>
            """.trimIndent()
            val document = Jsoup.parse("$template${payload?.content}")
            document.getElementsByTag("iframe").forEach { tag ->
                tag.append(" ")
            }
            document.html()
        }
    }
}