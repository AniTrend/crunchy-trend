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

package co.anitrend.support.crunchyroll.data.series.koin


import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.series.datasource.remote.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.series.mapper.SeriesResponseMapper
import co.anitrend.support.crunchyroll.data.series.repository.SeriesRepository
import co.anitrend.support.crunchyroll.data.series.source.SeriesSourceImpl
import co.anitrend.support.crunchyroll.data.series.usecase.SeriesInfoUseCaseImpl
import co.anitrend.support.crunchyroll.data.series.usecase.SeriesSearchUseCaseImpl
import co.anitrend.support.crunchyroll.domain.series.interactors.SeriesInfoUseCase
import co.anitrend.support.crunchyroll.domain.series.interactors.SeriesSearchUseCase
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        SeriesSourceImpl(
            mapper = get(),
            seriesDao = get<CrunchyDatabase>().crunchySeriesDao(),
            seriesEndpoint = CrunchySeriesEndpoint.create()
        )
    }
}

private val mapperModule = module {
    single {
        SeriesResponseMapper(
            dao = get<CrunchyDatabase>().crunchySeriesDao()
        )
    }
}

private val repositoryModule = module {
    factory {
        SeriesRepository(
            source = get<SeriesSourceImpl>()
        )
    }
}

private val useCaseModule = module {
    factory {
        SeriesInfoUseCaseImpl(
            repository = get()
        )
    }
    factory {
        SeriesSearchUseCaseImpl(
            repository = get()
        )
    }
}

val seriesModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule
)