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

package co.anitrend.support.crunchyroll.data.news.repository

import androidx.paging.PagedList
import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.arch.data.repository.SupportRepository
import co.anitrend.support.crunchyroll.data.news.source.contract.NewsSource
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.news.entities.News
import co.anitrend.support.crunchyroll.domain.news.repositories.INewsRepository

class NewsRepository(
    private val source: NewsSource
) : SupportRepository(source),
    INewsRepository<UserInterfaceState<PagedList<News>>> {

    override fun getNewsListings(query: RssQuery) =
        UserInterfaceState.create(
            model = source.newsObservable(query),
            source = source
        )

}