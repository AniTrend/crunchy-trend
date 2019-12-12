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

package co.anitrend.support.crunchyroll.data.stream.koin


import co.anitrend.support.crunchyroll.data.stream.datasource.remote.CrunchyStreamEndpoint
import co.anitrend.support.crunchyroll.data.stream.mapper.CrunchyStreamResponseMapper
import co.anitrend.support.crunchyroll.data.stream.repository.CrunchyStreamRepository
import co.anitrend.support.crunchyroll.data.stream.source.CrunchyStreamSourceImpl
import co.anitrend.support.crunchyroll.data.stream.usecase.CrunchyStreamUseCaseImpl
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        CrunchyStreamSourceImpl(
            endpoint = CrunchyStreamEndpoint.create(),
            mapper = get(),
            supportDispatchers = get(),
            supportConnectivity = get()
        )
    }
}

private val mapperModule = module {
    factory {
        CrunchyStreamResponseMapper()
    }
}

private val repositoryModule = module {
    factory {
        CrunchyStreamRepository(
            source = get<CrunchyStreamSourceImpl>()
        )
    }
}

private val useCaseModule = module {
    factory {
        CrunchyStreamUseCaseImpl(
            repository = get()
        )
    }
}

val streamModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule
)