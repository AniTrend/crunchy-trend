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

package co.anitrend.support.crunchyroll.data.news.helper

import co.anitrend.arch.extension.empty

object NewsHelper {
    private val imageRegex = Regex("<img src\\s*=\\s*\\\\*\"(.+?)\\\\*\"\\s*/>")
    private val breakLineRegex = Regex("<br.*?>")

    fun getImageSrc(content: String): String? {
        val matchResult = imageRegex.find(content)
        if (matchResult != null)
            return matchResult.groupValues[1]
        return null
    }

    fun getContentWithoutImage(content: String): List<String> {
        return content.replace(breakLineRegex, "")
            .split(imageRegex)
    }

    fun getSubTitle(content: List<String>): String {
        return content.firstOrNull()?.replace(
            breakLineRegex,
            ""
        ) ?: String.empty()
    }

    fun getShortDescription(content: List<String>): String {
        return if (content.size == 2)
            content[1].replace(imageRegex, "")
        else
            String.empty()
    }
}