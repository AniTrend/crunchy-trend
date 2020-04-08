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


import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.arch.extension.api
import co.anitrend.support.crunchyroll.data.arch.extension.db
import co.anitrend.support.crunchyroll.data.news.mapper.NewsResponseMapper
import co.anitrend.support.crunchyroll.data.news.repository.NewsRepository
import co.anitrend.support.crunchyroll.data.news.source.NewsSourceImpl
import co.anitrend.support.crunchyroll.data.news.source.contract.NewsSource
import co.anitrend.support.crunchyroll.data.news.usecase.NewsUseCaseImpl
import co.anitrend.support.crunchyroll.domain.news.interactors.NewsUseCase
import org.koin.dsl.bind
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        NewsSourceImpl(
            mapper = get(),
            endpoint = api(EndpointType.XML),
            dao = db().crunchyRssNewsDao(),
            supportDispatchers = get(),
            supportConnectivity = get()
        )
    } bind NewsSource::class
}

private val mapperModule = module {
    factory {
        NewsResponseMapper(
            dao = db().crunchyRssNewsDao()
        )
    }
}

private val repositoryModule = module {
    factory {
        NewsRepository(
            source = get()
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