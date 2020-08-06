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

package co.anitrend.support.crunchyroll.data.arch.helper

import co.anitrend.arch.data.request.contract.IRequestHelper
import co.anitrend.arch.extension.util.pagination.SupportPagingHelper
import timber.log.Timber

/**
 * Helper for configuring [SupportPagingHelper] to allow us to resume paging from the network
 */
internal object CrunchyPagingConfigHelper {

    private val moduleTag = javaClass.simpleName

    private fun setupPagingFrom(itemCount: Int, pagingHelper: SupportPagingHelper) {
        if (itemCount > 0) {
            val lastLoadedPage = itemCount / pagingHelper.pageSize
            pagingHelper.page = lastLoadedPage
            pagingHelper.pageOffset = itemCount
            Timber.tag(moduleTag).v(
                "Setting up paging -> items: $itemCount with pages: $lastLoadedPage"
            )
        }
    }

    /**
     * Since we plan on using a paging source backed by a database, Ideally we should
     * configure [pagingHelper] with the records count/paging limit to start load
     * from the last page of results in our backend.
     *
     * @param requestType Current request
     * @param pagingHelper paging helper
     * @param action what need to be run to return the number of available records
     */
    suspend inline operator fun invoke(
        requestType: IRequestHelper.RequestType,
        pagingHelper: SupportPagingHelper,
        crossinline action: suspend () -> Int
    ) {
        when (requestType) {
            IRequestHelper.RequestType.AFTER -> {
                Timber.tag(moduleTag).v(
                    "Triggered request: $requestType on paging helper configuration"
                )
                if (pagingHelper.isInitialAfterFirstLoad()) {
                    runCatching {
                        val count = action()
                        setupPagingFrom(count, pagingHelper)
                    }.exceptionOrNull()?.also {
                        Timber.tag(moduleTag).e(it)
                    }
                }
            }
            else -> {
                // We won't be support paging before a first item
                Timber.tag(moduleTag).v(
                    "Ignoring request: $requestType on paging helper configuration"
                )
            }
        }
    }

    private fun SupportPagingHelper.isInitialAfterFirstLoad() = page == 2
}