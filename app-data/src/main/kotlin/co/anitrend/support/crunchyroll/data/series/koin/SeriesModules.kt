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


import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.arch.extension.api
import co.anitrend.support.crunchyroll.data.arch.extension.db
import co.anitrend.support.crunchyroll.data.cache.repository.CacheLogStore
import co.anitrend.support.crunchyroll.data.series.helper.SeriesCacheHelper
import co.anitrend.support.crunchyroll.data.series.mapper.SeriesDetailResponseMapper
import co.anitrend.support.crunchyroll.data.series.mapper.SeriesResponseMapper
import co.anitrend.support.crunchyroll.data.series.repository.browse.SeriesBrowseRepository
import co.anitrend.support.crunchyroll.data.series.repository.detail.SeriesDetailRepository
import co.anitrend.support.crunchyroll.data.series.repository.search.SeriesSearchRepository
import co.anitrend.support.crunchyroll.data.series.source.browse.SeriesBrowseSourceImpl
import co.anitrend.support.crunchyroll.data.series.source.browse.contract.SeriesBrowseSource
import co.anitrend.support.crunchyroll.data.series.source.detail.SeriesDetailSourceImpl
import co.anitrend.support.crunchyroll.data.series.source.detail.contract.SeriesDetailSource
import co.anitrend.support.crunchyroll.data.series.source.search.SeriesSearchSourceImpl
import co.anitrend.support.crunchyroll.data.series.source.search.contract.SeriesSearchSource
import co.anitrend.support.crunchyroll.data.series.usecase.*
import co.anitrend.support.crunchyroll.data.series.usecase.SeriesBrowseUseCaseImpl
import co.anitrend.support.crunchyroll.data.series.usecase.SeriesDetailUseCaseImpl
import co.anitrend.support.crunchyroll.data.series.usecase.SeriesSearchUseCaseImpl
import org.koin.dsl.bind
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        SeriesDetailSourceImpl(
            mapper = get(),
            seriesDao = db().crunchySeriesDao(),
            endpoint = api(EndpointType.JSON),
            supportDispatchers = get(),
            settings = get(),
            cache = get(),
            supportConnectivity = get()
        )
    } bind SeriesDetailSource::class
    factory {
        SeriesSearchSourceImpl(
            mapper = get(),
            seriesDao = db().crunchySeriesDao(),
            endpoint = api(EndpointType.JSON),
            supportDispatchers = get(),
            settings = get(),
            supportConnectivity = get()
        )
    } bind SeriesSearchSource::class
    factory {
        SeriesBrowseSourceImpl(
            mapper = get(),
            seriesDao = db().crunchySeriesDao(),
            endpoint = api(EndpointType.JSON),
            supportDispatchers = get(),
            settings = get(),
            supportConnectivity = get()
        )
    } bind SeriesBrowseSource::class
}

private val mapperModule = module {
    factory {
        SeriesResponseMapper(
            dao = db().crunchySeriesDao()
        )
    }
    factory {
        SeriesDetailResponseMapper(
            dao = db().crunchySeriesDao()
        )
    }
}

private val repositoryModule = module {
    factory {
        SeriesDetailRepository(
            source = get()
        )
    }
    factory {
        SeriesSearchRepository(
            source = get()
        )
    }
    factory {
        SeriesBrowseRepository(
            source = get()
        )
    }
}

private val useCaseModule = module {
    factory<SeriesDetailUseCaseType> {
        SeriesDetailUseCaseImpl(
            repository = get()
        )
    }
    factory<SeriesSearchUseCaseType> {
        SeriesSearchUseCaseImpl(
            repository = get()
        )
    }
    factory<SeriesBrowseUseCaseType> {
        SeriesBrowseUseCaseImpl(
            repository = get()
        )
    }
}

private val cacheModule = module {
    factory {
        SeriesCacheHelper(
            dao = db().crunchyCacheDao()
        )
    }
}

val seriesModules = listOf(
    dataSourceModule, mapperModule, repositoryModule, useCaseModule, cacheModule
)