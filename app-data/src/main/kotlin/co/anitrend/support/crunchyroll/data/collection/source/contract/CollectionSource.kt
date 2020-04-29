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

package co.anitrend.support.crunchyroll.data.collection.source.contract

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper
import co.anitrend.arch.data.source.contract.ISourceObservable
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.data.arch.common.CrunchyPagedSource
import co.anitrend.support.crunchyroll.data.arch.database.dao.ISourceDao
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection
import co.anitrend.support.crunchyroll.domain.collection.models.CrunchyCollectionQuery

internal abstract class CollectionSource(
    supportDispatchers: SupportDispatchers
) : CrunchyPagedSource<CrunchyCollection>(supportDispatchers) {

    protected lateinit var query: CrunchyCollectionQuery
        private set

    protected abstract val collectionObservable:
            ISourceObservable<Nothing?, PagedList<CrunchyCollection>>

    protected abstract suspend fun getCollectionsForSeries(
        callback: PagingRequestHelper.Request.Callback,
        requestType: PagingRequestHelper.RequestType,
        model: CrunchyCollection?
    )

    internal operator fun invoke(param: CrunchyCollectionQuery): LiveData<PagedList<CrunchyCollection>> {
        query = param
        executionTarget = { callback: PagingRequestHelper.Request.Callback,
                            requestType: PagingRequestHelper.RequestType,
                            model: CrunchyCollection? ->
            getCollectionsForSeries(callback, requestType, model)
        }
        return collectionObservable(null)
    }
}