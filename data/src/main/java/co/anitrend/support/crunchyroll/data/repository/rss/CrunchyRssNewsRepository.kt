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

package co.anitrend.support.crunchyroll.data.repository.rss

import androidx.paging.PagedList
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssNews
import co.anitrend.support.crunchyroll.data.usecase.rss.CrunchyRssNewsUseCase
import io.wax911.support.data.repository.SupportRepository

class CrunchyRssNewsRepository(
    private val useCase: CrunchyRssNewsUseCase
) : SupportRepository<PagedList<CrunchyRssNews>, CrunchyRssNewsUseCase.Payload>() {
    /**
     * Handles dispatching of network requests to a background thread
     *
     * @param subject subject to apply business rules
     */
    override fun invoke(subject: CrunchyRssNewsUseCase.Payload) =
        useCase(
            param = subject
        )
}