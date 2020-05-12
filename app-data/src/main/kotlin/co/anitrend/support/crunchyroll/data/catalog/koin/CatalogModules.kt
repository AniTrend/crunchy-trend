/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.data.catalog.koin

import co.anitrend.support.crunchyroll.data.arch.extension.db
import co.anitrend.support.crunchyroll.data.cache.repository.CacheLogStore
import co.anitrend.support.crunchyroll.data.catalog.helper.CatalogCacheHelper
import co.anitrend.support.crunchyroll.data.catalog.mapper.CatalogResponseMapper
import co.anitrend.support.crunchyroll.data.catalog.repository.CatalogRepository
import co.anitrend.support.crunchyroll.data.catalog.source.CatalogSourceImpl
import co.anitrend.support.crunchyroll.data.catalog.source.contract.CatalogSource
import co.anitrend.support.crunchyroll.data.catalog.usecase.CatalogUseCaseImpl
import co.anitrend.support.crunchyroll.data.catalog.usecase.CatalogUseCaseType
import org.koin.dsl.bind
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        CatalogSourceImpl(
            catalogDao = db().crunchyCatalogDao(),
            supportConnectivity = get(),
            supportDispatchers = get(),
            batchSource = get(),
            settings = get(),
            mapper = get(),
            cache = get()
        )
    } bind CatalogSource::class
}

private val mapperModule = module {
    factory {
        CatalogResponseMapper(
            dao = db().crunchyCatalogDao(),
            seriesResponseMapper = get()
        )
    }
}

private val repositoryModule = module {
    factory {
        CatalogRepository(
            source = get()
        )
    }
}

private val useCaseModule = module {
    factory<CatalogUseCaseType> {
        CatalogUseCaseImpl(
            repository = get()
        )
    }
}

private val cacheModule = module {
    factory {
        CatalogCacheHelper(
            dao = db().crunchyCacheDao()
        )
    }
}

val catalogModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule, cacheModule
)