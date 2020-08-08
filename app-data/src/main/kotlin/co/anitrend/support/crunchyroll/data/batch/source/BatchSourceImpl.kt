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

package co.anitrend.support.crunchyroll.data.batch.source

import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import co.anitrend.arch.extension.network.SupportConnectivity
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OfflineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.controller.strategy.policy.OnlineControllerPolicy
import co.anitrend.support.crunchyroll.data.arch.extension.controller
import co.anitrend.support.crunchyroll.data.batch.datasource.remote.CrunchyBatchEndpoint
import co.anitrend.support.crunchyroll.data.batch.mapper.BatchResponseMapper
import co.anitrend.support.crunchyroll.data.batch.source.contract.BatchSource
import co.anitrend.support.crunchyroll.data.batch.usecase.model.CrunchyBatchQuery
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow

internal class BatchSourceImpl(
    private val endpoint: CrunchyBatchEndpoint,
    private val supportConnectivity: SupportConnectivity,
    private val mapper: BatchResponseMapper,
    supportDispatchers: SupportDispatchers
) : BatchSource(supportDispatchers) {

    override fun getBatchOfSeries(
        queries: List<CrunchyBatchQuery>,
        callback: RequestCallback
    ) = flow {
        val deferred = async {
            endpoint.requestInBatch(
                request = CrunchyBatchQuery.toJson(queries)
            )
        }
        val controller =
            mapper.controller(dispatchers, OfflineControllerPolicy.create())

        emit(controller(deferred, callback))
    }
}
