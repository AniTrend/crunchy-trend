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

package co.anitrend.support.crunchyroll.data.news.transformer

import co.anitrend.arch.data.mapper.contract.ISupportMapperHelper
import co.anitrend.support.crunchyroll.data.news.entity.NewsEntity
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews

internal object NewsTransformer : ISupportMapperHelper<NewsEntity, CrunchyNews> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: NewsEntity): CrunchyNews {
        return CrunchyNews(
            title = source.title,
            image = source.image,
            author = source.author,
            subTitle = source.subTitle,

            description = source.description,
            content = source.content,
            publishedOn = source.publishedOn
        )
    }
}