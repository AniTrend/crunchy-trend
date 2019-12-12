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

package co.anitrend.support.crunchyroll.data.episode.koin


import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.episode.datasource.remote.CrunchyEpisodeFeedEndpoint
import co.anitrend.support.crunchyroll.data.episode.mapper.EpisodeFeedResponseMapper
import co.anitrend.support.crunchyroll.data.episode.repository.EpisodeFeedRepository
import co.anitrend.support.crunchyroll.data.episode.source.EpisodeFeedSourceImpl
import co.anitrend.support.crunchyroll.data.episode.usecase.EpisodeFeedUseCaseImpl
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        EpisodeFeedSourceImpl(
            mapper = get(),
            endpoint = CrunchyEpisodeFeedEndpoint.create(),
            dao = get<CrunchyDatabase>().crunchyRssMediaDao(),
            supportDispatchers = get(),
            supportConnectivity = get()
        )
    }
}

private val mapperModule = module {
    factory {
        EpisodeFeedResponseMapper(
            dao = get<CrunchyDatabase>().crunchyRssMediaDao(),
            localeHelper = get(),
            settings = get()
        )
    }
}

private val repositoryModule = module {
    factory {
        EpisodeFeedRepository(
            source = get<EpisodeFeedSourceImpl>()
        )
    }
}

private val useCaseModule = module {
    factory {
        EpisodeFeedUseCaseImpl(
            repository = get()
        )
    }
}

val episodeModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule
)