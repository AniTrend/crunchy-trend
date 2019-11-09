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

package co.anitrend.support.crunchyroll.data.transformer

import androidx.annotation.VisibleForTesting
import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssNews
import co.anitrend.support.crunchyroll.domain.entities.result.rss.News

object NewsTransformer : ISupportMapperHelper<CrunchyRssNews, News> {

    private val regex = Regex("<img src\\s*=\\s*\\\\*\"(.+?)\\\\*\"\\s*/>")
    private val breakLine = Regex("<br.*?>")

    @VisibleForTesting
    fun String.getImageSrc(): String? {
        val matchResult = regex.find(this)
        if (matchResult != null)
            return matchResult.groupValues[1]
        return null
    }

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyRssNews): News {
        val imageSource = source.description.getImageSrc()
        val content = source.description.replace(breakLine, "").split(regex)
        val subTitle = content[0].replace(breakLine, "")
        val quickDescription = content[1].replaceFirst(regex, "")

        return News(
            title = source.title,
            image = imageSource,
            author = source.author,
            subTitle = subTitle,
            description = quickDescription,
            content = source.encoded,
            publishedOn = source.publishedTime
        )
    }
}