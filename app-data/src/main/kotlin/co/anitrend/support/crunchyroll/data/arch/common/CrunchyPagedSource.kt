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

package co.anitrend.support.crunchyroll.data.arch.common

import androidx.paging.PagedList
import androidx.paging.PagingRequestHelper.Request
import androidx.paging.PagingRequestHelper.RequestType
import co.anitrend.arch.data.source.paging.SupportPagingDataSource
import co.anitrend.arch.extension.dispatchers.SupportDispatchers
import kotlinx.coroutines.launch

/**
 * Helper for prototyping quick sources
 */
internal abstract class CrunchyPagedSource<T>(
    supportDispatchers: SupportDispatchers
) : SupportPagingDataSource<T>(supportDispatchers) {

    @Volatile
    protected lateinit var executionTarget: suspend (
        callback: Request.Callback,
        requestType: RequestType,
        model: T?
    ) -> Unit

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        pagingRequestHelper.runIfNotRunning(
            RequestType.INITIAL
        ) { pagingRequestCallback ->
            launch {
                executionTarget(
                    pagingRequestCallback,
                    RequestType.INITIAL,
                    null
                )
            }
        }
    }

    /**
     * Called when the item at the end of the PagedList has been loaded, and access has
     * occurred within [PagedList.Config.prefetchDistance] of it.
     *
     * No more data will be appended to the PagedList after this item.
     *
     * @param itemAtEnd The first item of PagedList
     */
    override fun onItemAtEndLoaded(itemAtEnd: T) {
        pagingRequestHelper.runIfNotRunning(
            RequestType.AFTER
        ) { pagingRequestCallback ->
            launch {
                supportPagingHelper.onPageNext()
                executionTarget(
                    pagingRequestCallback,
                    RequestType.AFTER,
                    itemAtEnd
                )
            }
        }
    }
}