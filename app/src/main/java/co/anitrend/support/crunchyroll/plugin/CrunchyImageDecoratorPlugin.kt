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

package co.anitrend.support.crunchyroll.plugin

import io.noties.markwon.AbstractMarkwonPlugin

class CrunchyImageDecoratorPlugin private constructor() : AbstractMarkwonPlugin() {

    override fun processMarkdown(markdown: String): String {
        val matchResult = imageSpanRegex.find(markdown)
        if (matchResult != null) {
            val result = matchResult.value
            return markdown.replace(result, """<center>$result</center>""")
        }
        return markdown
    }

    companion object {
        private val imageSpanRegex = Regex("<img.*/>")
        fun create() = CrunchyImageDecoratorPlugin()
    }
}