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

    fun getContentWithoutImage(content: String): String {
        val matches = content.replace(breakLineRegex, "").split(imageRegex)
        return matches.firstOrNull() ?: String.empty()
    }

    fun getSubTitle(content: String): String {
        return content.replace(breakLineRegex, "")
    }

    fun getShortDescription(content: String): String {
        return content.replaceFirst(imageRegex, "")
    }
}