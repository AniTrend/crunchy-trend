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

package co.anitrend.support.crunchyroll.feature.news.plugin

import co.anitrend.support.crunchyroll.feature.news.plugin.decorator.EmptyTagHandler
import co.anitrend.support.crunchyroll.feature.news.plugin.decorator.FrameTagHandler
import co.anitrend.support.crunchyroll.feature.news.plugin.decorator.ParagraphTagHandler
import co.anitrend.support.crunchyroll.feature.news.plugin.decorator.TagAlignmentHandler
import co.anitrend.support.crunchyroll.feature.news.plugin.spans.ImageTagHandler
import io.noties.markwon.*
import io.noties.markwon.html.HtmlPlugin
import org.commonmark.node.Paragraph
import timber.log.Timber

internal class CrunchyTagPlugin private constructor(): AbstractMarkwonPlugin() {

    override fun configure(registry: MarkwonPlugin.Registry) {
        registry.require(HtmlPlugin::class.java) { plugin ->
            plugin.emptyTagReplacement(EmptyTagHandler.create())
            plugin.addHandler(FrameTagHandler.create())
            plugin.addHandler(ImageTagHandler.create())
            //plugin.addHandler(ParagraphTagHandler.create())
            //plugin.addHandler(TagAlignmentHandler.create())
        }
    }

    companion object {
        fun create() = CrunchyTagPlugin()
    }
}