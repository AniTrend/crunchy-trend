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

import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyRssMapper
import co.anitrend.support.crunchyroll.data.news.datasource.local.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.news.datasource.local.transformer.NewsEntityTransformer
import co.anitrend.support.crunchyroll.data.news.entity.NewsEntity
import co.anitrend.support.crunchyroll.data.news.model.CrunchyNewsModel
import co.anitrend.support.crunchyroll.data.rss.contract.ICrunchyRssChannel

class NewsResponseMapper(
    private val dao: CrunchyRssNewsDao
) : CrunchyRssMapper<CrunchyNewsModel, NewsEntity>() {

    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     * called in [retrofit2.Callback.onResponse] after assuring that the response was a success
     *
     * @param source the incoming data source type
     * @return Mapped object that will be consumed by [onResponseDatabaseInsert]
     * @see [ISupportResponseHelper.invoke]
     */
    override suspend fun onResponseMapFrom(source: ICrunchyRssChannel<CrunchyNewsModel>): List<NewsEntity> {
        return source.item?.map {
            NewsEntityTransformer.transform(
                it.copy(
                    copyright = source.copyright
                )
            )
        } ?: emptyList()
    }

    /**
     * Inserts the given object into the implemented room database,
     * called in [retrofit2.Callback.onResponse]
     *
     * @param mappedData mapped object from [onResponseMapFrom] to insert into the database
     * @see [ISupportResponseHelper.invoke]
     */
    override suspend fun onResponseDatabaseInsert(mappedData: List<NewsEntity>) {
        if (mappedData.isNotEmpty())
            dao.upsert(mappedData)
    }
}