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

package co.anitrend.support.crunchyroll.data.series.source.detail.contract

import androidx.lifecycle.LiveData
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.data.source.core.SupportCoreDataSource
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.domain.series.models.CrunchySeriesDetailQuery
import kotlinx.coroutines.launch

internal abstract class SeriesDetailSource(
    supportDispatchers: SupportDispatchers
) : SupportCoreDataSource(supportDispatchers) {

    protected lateinit var query: CrunchySeriesDetailQuery
        private set

    abstract val detailObservable:
            ISourceObservable<Nothing?, CrunchySeries?>

    protected abstract suspend fun browseSeries()

    internal operator fun invoke(detailQuery: CrunchySeriesDetailQuery): LiveData<CrunchySeries?> {
        query = detailQuery
        retry = { launch { browseSeries() } }
        launch { browseSeries() }
        return detailObservable(null)
    }
}