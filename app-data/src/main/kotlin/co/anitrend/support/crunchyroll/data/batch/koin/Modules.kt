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

package co.anitrend.support.crunchyroll.data.batch.koin

import co.anitrend.support.crunchyroll.data.api.contract.EndpointType
import co.anitrend.support.crunchyroll.data.arch.extension.api
import co.anitrend.support.crunchyroll.data.batch.mapper.BatchResponseMapper
import co.anitrend.support.crunchyroll.data.batch.source.BatchSourceImpl
import co.anitrend.support.crunchyroll.data.batch.source.contract.BatchSource
import org.koin.dsl.bind
import org.koin.dsl.module

private val dataSourceModule = module {
    factory {
        BatchSourceImpl(
            endpoint = api(EndpointType.JSON),
            supportConnectivity = get(),
            mapper = BatchResponseMapper(),
            supportDispatchers = get()
        )
    } bind BatchSource::class
}

val batchModules = listOf(
    dataSourceModule
)