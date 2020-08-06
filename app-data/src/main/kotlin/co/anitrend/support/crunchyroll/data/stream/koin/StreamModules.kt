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


import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.arch.extension.api
import co.anitrend.support.crunchyroll.data.arch.extension.db
import co.anitrend.support.crunchyroll.data.series.helper.SeriesCacheHelper
import co.anitrend.support.crunchyroll.data.stream.helper.StreamCacheHelper
import co.anitrend.support.crunchyroll.data.stream.mapper.CrunchyStreamResponseMapper
import co.anitrend.support.crunchyroll.data.stream.repository.CrunchyStreamRepository
import co.anitrend.support.crunchyroll.data.stream.source.CrunchyStreamSourceImpl
import co.anitrend.support.crunchyroll.data.stream.source.contract.CrunchyStreamSource
import co.anitrend.support.crunchyroll.data.stream.usecase.CrunchyStreamUseCaseImpl
import co.anitrend.support.crunchyroll.data.stream.usecase.MediaStreamUseCaseType
import org.koin.dsl.bind
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        CrunchyStreamSourceImpl(
            streamDao = db().crunchyStreamDao(),
            endpoint = api(EndpointType.JSON),
            mapper = get(),
            supportDispatchers = get(),
            supportConnectivity = get(),
            settings = get(),
            cache = get()
        )
    } bind CrunchyStreamSource::class
}

private val mapperModule = module {
    factory {
        CrunchyStreamResponseMapper(
            dao = db().crunchyStreamDao()
        )
    }
}

private val repositoryModule = module {
    factory {
        CrunchyStreamRepository(
            source = get()
        )
    }
}

private val useCaseModule = module {
    factory<MediaStreamUseCaseType> {
        CrunchyStreamUseCaseImpl(
            repository = get()
        )
    }
}

private val cacheModule = module {
    factory {
        StreamCacheHelper(
            dao = db().crunchyCacheDao()
        )
    }
}

val streamModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule, cacheModule
)