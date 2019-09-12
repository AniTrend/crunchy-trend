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
import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.arch.data.repository.SupportRepository
import co.anitrend.support.crunchyroll.data.datasource.auto.rss.contract.MediaListingSource
import co.anitrend.support.crunchyroll.domain.entities.query.rss.RssQuery
import co.anitrend.support.crunchyroll.domain.entities.result.rss.MediaListing
import co.anitrend.support.crunchyroll.domain.repositories.rss.IMediaListingRepository

class MediaListingRepository(
    private val source: MediaListingSource
) : SupportRepository(source),
    IMediaListingRepository<UserInterfaceState<PagedList<MediaListing>>> {

    override fun getMediaListings(query: RssQuery) =
        UserInterfaceState.create(
            model = source.mediaListingsObservable(query),
            source = source
        )

}