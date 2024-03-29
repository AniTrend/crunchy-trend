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

package co.anitrend.support.crunchyroll.data.catalog.source.contract

import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.model.Request
import co.anitrend.arch.data.source.core.SupportCoreDataSource
import co.anitrend.arch.extension.dispatchers.contract.ISupportDispatcher
import co.anitrend.support.crunchyroll.domain.catalog.entities.CrunchyCatalogWithSeries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal abstract class CatalogSource : SupportCoreDataSource() {

    protected abstract fun observable(): Flow<List<CrunchyCatalogWithSeries>>

    protected abstract suspend fun getCatalog(callback: RequestCallback)

    fun catalogSeries(): Flow<List<CrunchyCatalogWithSeries>> {
        launch {
            requestHelper.runIfNotRunning(
                Request.Default(
                    "catalog_source_initial",
                    Request.Type.INITIAL
                )
            ) { getCatalog(it) }
        }
        return observable()
    }
}