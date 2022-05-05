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

package co.anitrend.support.crunchyroll.data.news.mapper

import co.anitrend.support.crunchyroll.data.arch.mapper.DefaultMapper
import co.anitrend.support.crunchyroll.data.news.datasource.local.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.news.datasource.local.transformer.NewsEntityTransformer
import co.anitrend.support.crunchyroll.data.news.entity.NewsEntity
import co.anitrend.support.crunchyroll.data.news.model.CrunchyNewsModel
import co.anitrend.support.crunchyroll.data.news.model.page.CrunchyNewsPageModel
import co.anitrend.support.crunchyroll.data.rss.contract.ICrunchyRssChannel

internal class NewsResponseMapper(
    private val dao: CrunchyRssNewsDao
) : DefaultMapper<CrunchyNewsPageModel, List<NewsEntity>>() {

    /**
     * Save [data] into your desired local source
     */
    override suspend fun persist(data: List<NewsEntity>) {
        dao.upsert(data)
    }

    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     *
     * @param source the incoming data source type
     * @return mapped object that will be consumed by [onResponseDatabaseInsert]
     */
    override suspend fun onResponseMapFrom(
        source: CrunchyNewsPageModel
    ): List<NewsEntity> {
        return source.channel?.items?.map {
            NewsEntityTransformer.transform(it)
        }.orEmpty()
    }
}