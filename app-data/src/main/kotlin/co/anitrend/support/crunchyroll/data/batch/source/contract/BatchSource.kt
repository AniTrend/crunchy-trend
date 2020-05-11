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

package co.anitrend.support.crunchyroll.data.batch.source.contract

import androidx.lifecycle.MutableLiveData
import co.anitrend.arch.data.source.coroutine.SupportCoroutineDataSource
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.data.batch.entity.CrunchyBatchEntity
import co.anitrend.support.crunchyroll.data.batch.usecase.model.CrunchyBatchQuery
import co.anitrend.support.crunchyroll.data.series.model.CrunchySeriesModel

internal abstract class BatchSource(
    supportDispatchers: SupportDispatchers
) : SupportCoroutineDataSource(supportDispatchers) {

    abstract suspend fun getBatchOfSeries(
        queries: List<CrunchyBatchQuery>,
        networkState: MutableLiveData<NetworkState>
    ): List<CrunchyBatchEntity<CrunchySeriesModel>>?
}