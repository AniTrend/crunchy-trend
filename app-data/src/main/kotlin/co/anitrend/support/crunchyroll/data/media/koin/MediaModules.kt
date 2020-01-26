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


import co.anitrend.support.crunchyroll.data.media.mapper.MediaResponseMapper
import co.anitrend.support.crunchyroll.data.media.repository.MediaRepository
import co.anitrend.support.crunchyroll.data.media.source.MediaSourceImpl
import co.anitrend.support.crunchyroll.data.media.usecase.MediaUseCaseImpl
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        MediaSourceImpl(
            supportConnectivity = get(),
            supportDispatchers = get(),
            mapper = get(),
            mediaDao = get(),
            mediaEndpoint = get()
        )
    }
}

private val mapperModule = module {
    factory {
        MediaResponseMapper(
            dao = get()
        )
    }
}

private val repositoryModule = module {
    factory {
        MediaRepository(
            source = get<MediaSourceImpl>()
        )
    }
}

private val useCaseModule = module {
    factory {
        MediaUseCaseImpl(
            repository = get()
        )
    }
}

val mediaModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule
)