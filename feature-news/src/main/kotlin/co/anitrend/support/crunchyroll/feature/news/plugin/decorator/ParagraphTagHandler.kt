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

import android.text.Layout
import android.text.style.AlignmentSpan
import android.text.style.LeadingMarginSpan
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler

/**
 * Handles attributes on paragraph tags
 *
 * <p style="text-align: center;"></p>
 * <p style="text-align: left;"></p>
 * <p style="text-align: right;"></p>
 * <p style="margin-left %%px;"></p>
 * <p class="p%"></p>
 */
internal class ParagraphTagHandler private constructor(): SimpleTagHandler() {

    override fun getSpans(
        configuration: MarkwonConfiguration,
        renderProps: RenderProps,
        tag: HtmlTag
    ): Any? {
        val attributes = tag.attributes()
        val style = attributes["style"]
        val styleClass = attributes["class"]

        val layoutAlignment = when (style) {
            CENTER_ALIGN -> Layout.Alignment.ALIGN_CENTER
            RIGHT_ALIGN -> Layout.Alignment.ALIGN_OPPOSITE
            else -> null
        }
        if (layoutAlignment != null)
           return AlignmentSpan.Standard(layoutAlignment)

        val leadingMarginSize = if (style?.startsWith(MARGIN_PREFIX) == true) {
            // e.g. margin-left: 30px;
            val length = style.length
            val start = length - 5
            val end = length - 3

            val paddingSize = style.substring(start, end).toInt()
            paddingSize
        } else null

        if (leadingMarginSize != null)
            return LeadingMarginSpan.Standard(leadingMarginSize)

        return null
    }

    override fun supportedTags(): List<String> = listOf("p")

    companion object {
        private const val CENTER_ALIGN = "text-align: center;"
        private const val RIGHT_ALIGN = "text-align: right;"
        private const val MARGIN_PREFIX = "margin-left"

        fun create() = ParagraphTagHandler()
    }
}