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
import androidx.paging.PagingRequestHelper
import co.anitrend.arch.data.source.paging.SupportPagingDataSource
import co.anitrend.arch.extension.SupportDispatchers
import co.anitrend.support.crunchyroll.data.arch.CrunchyExperimentalFeature
import co.anitrend.support.crunchyroll.data.arch.database.dao.ISourceDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Helper for prototyping quick sources
 */
abstract class CrunchyPagedSource<T>(
    supportDispatchers: SupportDispatchers,
    sourceDao: ISourceDao
) : SupportPagingDataSource<T>(supportDispatchers) {

    init {
        launch (dispatchers.io) {
            @OptIn(CrunchyExperimentalFeature::class)
            configurePagingHelper(sourceDao)
        }
    }

    protected lateinit var executionTarget: (PagingRequestHelper.Request.Callback) -> Unit

    /**
     * Since we plan on using a paging source backed by a database, Ideally we should
     * configure [supportPagingHelper] with the records count/paging limit to start load
     * from the last page of results in our backend.
     *
     * @param sourceDao contract for all compatible data access objects
     *
     * @see setUpPagingHelperWithInitial
     */
    @CrunchyExperimentalFeature
    protected open suspend fun configurePagingHelper(sourceDao: ISourceDao) {
        // Disabled for now since not all pagination can be calculated without supplying
        // a filter clause
    }

    protected fun setUpPagingHelperWithInitial(itemsCount: Int) {
        if (itemsCount > 0) {
            val lastLoadedPage = itemsCount / supportPagingHelper.pageSize
            supportPagingHelper.page = lastLoadedPage
            supportPagingHelper.pageOffset = lastLoadedPage * supportPagingHelper.pageSize
        }
    }

    /**
     * Called when zero items are returned from an initial load of the PagedList's data source.
     */
    override fun onZeroItemsLoaded() {
        pagingRequestHelper.runIfNotRunning(
            PagingRequestHelper.RequestType.INITIAL
        ) {
            executionTarget(it)
        }
    }

    /**
     * Called when the item at the end of the PagedList has been loaded, and access has
     * occurred within [PagedList.Config.prefetchDistance] of it.
     *
     *
     * No more data will be appended to the PagedList after this item.
     *
     * @param itemAtEnd The first item of PagedList
     */
    override fun onItemAtEndLoaded(itemAtEnd: T) {
        pagingRequestHelper.runIfNotRunning(
            PagingRequestHelper.RequestType.AFTER
        ) {
            supportPagingHelper.onPageNext()
            executionTarget(it)
        }
    }
}