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

package co.anitrend.support.crunchyroll.feature.news.plugin.decorator

import android.annotation.SuppressLint
import io.noties.markwon.html.HtmlEmptyTagReplacement
import io.noties.markwon.html.HtmlTag

internal class EmptyTagHandler private constructor() : HtmlEmptyTagReplacement() {

    /**
     * @return replacement for supplied startTag or null if no replacement should occur (which will
     * lead to `Inline` tag have start &amp; end the same value, thus not applicable for applying a Span)
     */
    @SuppressLint("DefaultLocale")
    override fun replace(tag: HtmlTag): String? {
        return when (tag.name().toLowerCase()) {
            in handlingTags -> {
                frameReplacement
            }
            else -> super.replace(tag)
        }
    }

    companion object {
        private const val frameReplacement = "\u00a0" // non-breakable space
        private val handlingTags = listOf("iframe")

        fun create() = EmptyTagHandler()
    }
}