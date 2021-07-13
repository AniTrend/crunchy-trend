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

package co.anitrend.support.crunchyroll.data.media.koin


import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.arch.extension.api
import co.anitrend.support.crunchyroll.data.arch.extension.db
import co.anitrend.support.crunchyroll.data.arch.extension.defaultController
import co.anitrend.support.crunchyroll.data.media.mapper.MediaResponseMapper
import co.anitrend.support.crunchyroll.data.media.repository.MediaRepository
import co.anitrend.support.crunchyroll.data.media.source.MediaSourceImpl
import co.anitrend.support.crunchyroll.data.media.source.contract.MediaSource
import co.anitrend.support.crunchyroll.data.media.usecase.MediaUseCaseImpl
import co.anitrend.support.crunchyroll.data.media.usecase.MediaUseCaseType
import org.koin.dsl.bind
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        MediaSourceImpl(
            supportConnectivity = get(),
            dispatcher = get(),
            controller = defaultController(
                mapper = get<MediaResponseMapper>()
            ),
            mediaDao = db().crunchyMediaDao(),
            endpoint = api(EndpointType.JSON),
            settings = get()
        )
    } bind MediaSource::class
}

private val mapperModule = module {
    factory {
        MediaResponseMapper(
            dao = db().crunchyMediaDao()
        )
    }
}

private val repositoryModule = module {
    factory {
        MediaRepository(
            source = get()
        )
    }
}

private val useCaseModule = module {
    factory<MediaUseCaseType> {
        MediaUseCaseImpl(
            repository = get()
        )
    }
}

val mediaModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule
)