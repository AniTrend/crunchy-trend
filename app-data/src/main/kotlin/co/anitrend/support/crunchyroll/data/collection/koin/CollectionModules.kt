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

package co.anitrend.support.crunchyroll.data.collection.koin

import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.arch.extension.api
import co.anitrend.support.crunchyroll.data.arch.extension.db
import co.anitrend.support.crunchyroll.data.collection.mapper.CollectionResponseMapper
import co.anitrend.support.crunchyroll.data.collection.repository.CollectionRepository
import co.anitrend.support.crunchyroll.data.collection.source.CollectionSourceImpl
import co.anitrend.support.crunchyroll.data.collection.source.contract.CollectionSource
import co.anitrend.support.crunchyroll.data.collection.usecase.CollectionUseCaseImpl
import co.anitrend.support.crunchyroll.domain.collection.interactors.CollectionUseCase
import org.koin.dsl.bind
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        CollectionSourceImpl(
            mapper = get(),
            collectionDao = db().crunchyCollectionDao(),
            collectionEndpoint = api(EndpointType.JSON),
            supportDispatchers = get(),
            supportConnectivity = get()
        )
    } bind CollectionSource::class
}

private val mapperModule = module {
    factory {
        CollectionResponseMapper(
            dao = db().crunchyCollectionDao()
        )
    }
}

private val repositoryModule = module {
    factory {
        CollectionRepository(
            source = get()
        )
    }
}

private val useCaseModule = module {
    factory {
        CollectionUseCaseImpl(
            repository = get()
        )
    }
}

val collectionModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule
)