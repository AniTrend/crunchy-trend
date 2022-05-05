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

package co.anitrend.support.crunchyroll.data.collection.repository

import androidx.paging.PagedList
import co.anitrend.arch.data.repository.SupportRepository
import co.anitrend.arch.data.state.DataState
import co.anitrend.arch.data.state.DataState.Companion.create
import co.anitrend.support.crunchyroll.data.collection.source.contract.CollectionSource
import co.anitrend.support.crunchyroll.domain.collection.entities.CrunchyCollection
import co.anitrend.support.crunchyroll.domain.collection.models.CrunchyCollectionQuery
import co.anitrend.support.crunchyroll.domain.collection.repositories.ICollectionRepository

internal class CollectionRepository(
    private val source: CollectionSource
) : SupportRepository(source),
    ICollectionRepository<DataState<PagedList<CrunchyCollection>>> {

    override fun getCollection(
        seriesQuery: CrunchyCollectionQuery
    ) = source create source.collection(seriesQuery)

}