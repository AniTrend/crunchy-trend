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

import co.anitrend.support.crunchyroll.feature.news.plugin.decorator.FrameTagHandler
import co.anitrend.support.crunchyroll.feature.news.plugin.decorator.TagAlignmentHandler
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.html.HtmlPlugin

class CrunchyTagPlugin private constructor(): AbstractMarkwonPlugin() {

    override fun configure(registry: MarkwonPlugin.Registry) {
        registry.require(HtmlPlugin::class.java) {
            it.addHandler(TagAlignmentHandler.create())
            it.addHandler(FrameTagHandler.create())
        }
    }

    companion object {
        fun create() = CrunchyTagPlugin()
    }
}