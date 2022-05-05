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
import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.data.request.model.Request
import co.anitrend.arch.data.source.paging.SupportPagingDataSource
import kotlinx.coroutines.launch

/**
 * Helper for prototyping quick sources
 */
internal abstract class CrunchyPagedSource<T> : SupportPagingDataSource<T>() {

    abstract suspend operator fun invoke(
        callback: RequestCallback,
        request: Request,
        model: T? = null
    )

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        val request = Request.Default("${javaClass.simpleName}_initial", Request.Type.INITIAL)
        launch {
            requestHelper.runIfNotRunning(request) { callback ->
                invoke(callback, request)
            }
        }
    }

    /**
     * Called when the item at the front of the PagedList has been loaded, and access has
     * occurred within [Config.prefetchDistance] of it.
     *
     * No more data will be prepended to the PagedList before this item.
     *
     * @param itemAtFront The first item of PagedList
     */
    override fun onItemAtFrontLoaded(itemAtFront: T) {
        if (!supportPagingHelper.isFirstPage()) {
            val request = Request.Default("${javaClass.simpleName}_before", Request.Type.BEFORE)
            /*supportPagingHelper.onPagePrevious()
            launch {
                requestHelper.runIfNotRunning(request) { callback ->
                    invoke(callback, request, itemAtFront)
                }
            }*/
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
        val request = Request.Default("${javaClass.simpleName}_after", Request.Type.AFTER)
        supportPagingHelper.onPageNext()
        launch {
            requestHelper.runIfNotRunning(request) { callback ->
                invoke(callback, request, itemAtEnd)
            }
        }
    }
}