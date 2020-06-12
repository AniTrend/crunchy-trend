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
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.MarkwonHtmlRenderer
import io.noties.markwon.html.TagHandler

/**
 * Handles attributes on paragraph tags
 *
 * <p style="text-align: center;"></p>
 * <p style="text-align: left;"></p>
 * <p style="text-align: right;"></p>
 * <p style="margin-left %%px;"></p>
 * <p class="p%"></p>
 */
internal class ParagraphTagHandler private constructor(): TagHandler() {

    override fun handle(
        visitor: MarkwonVisitor,
        renderer: MarkwonHtmlRenderer,
        tag: HtmlTag
    ) {
        val spans = ArrayList<Any>(2)
        val attributes = tag.attributes()
        val style = attributes["style"]
        val styleClass = attributes["class"]

        if (tag.isBlock)
            visitChildren(visitor, renderer, tag.asBlock)

        if (style == CENTER_ALIGN)
            spans.add(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER))
        else if (style == RIGHT_ALIGN)
            spans.add(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE))

        if (style?.startsWith(MARGIN_PREFIX) == true) {
            // e.g. margin-left: 30px;
            val length = style.length
            val start = length - 5
            val end = length - 3

            val paddingSize = style.substring(start, end).toInt()
            spans.add(LeadingMarginSpan.Standard(paddingSize))
        }

        if (spans.isNotEmpty())
            SpannableBuilder.setSpans(
                visitor.builder(),
                spans.toArray(),
                tag.start(),
                tag.end()
            )

    }

    override fun supportedTags(): List<String> = listOf("p")

    companion object {
        private const val CENTER_ALIGN = "text-align: center;"
        private const val RIGHT_ALIGN = "text-align: right;"
        private const val MARGIN_PREFIX = "margin-left"

        fun create() = ParagraphTagHandler()
    }
}