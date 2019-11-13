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

package co.anitrend.support.crunchyroll.data.news.koin


import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.news.datasource.remote.CrunchyNewsFeedEndpoint
import co.anitrend.support.crunchyroll.data.news.mapper.NewsResponseMapper
import co.anitrend.support.crunchyroll.data.news.repository.NewsRepository
import co.anitrend.support.crunchyroll.data.news.source.NewsSourceImpl
import co.anitrend.support.crunchyroll.data.news.usecase.NewsUseCaseImpl
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        NewsSourceImpl(
            responseMapper = get(),
            endpoint = CrunchyNewsFeedEndpoint.create(),
            dao = get<CrunchyDatabase>().crunchyRssNewsDao()
        )
    }
}

private val mapperModule = module {
    factory {
        NewsResponseMapper(
            dao = get<CrunchyDatabase>().crunchyRssNewsDao()
        )
    }
}

private val repositoryModule = module {
    factory {
        NewsRepository(
            source = get<NewsSourceImpl>()
        )
    }
}

private val useCaseModule = module {
    factory {
        NewsUseCaseImpl(
            repository = get()
        )
    }
}

val newsModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule
)