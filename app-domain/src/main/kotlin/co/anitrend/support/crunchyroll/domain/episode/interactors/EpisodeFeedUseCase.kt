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

package co.anitrend.support.crunchyroll.domain.episode.interactors

import co.anitrend.arch.domain.common.IUserInterfaceState
import co.anitrend.arch.domain.usecases.ISupportUseCase
import co.anitrend.support.crunchyroll.domain.common.RssQuery
import co.anitrend.support.crunchyroll.domain.episode.repositories.IEpisodeFeedRepository

abstract class EpisodeFeedUseCase<R: IUserInterfaceState<*>>(
    protected val repository: IEpisodeFeedRepository<R>
) : ISupportUseCase<RssQuery, R> {

    /**
     * Solves a given use case in the implementation target
     *
     * @param param input for solving a given use case
     */
    override fun invoke(param: RssQuery): R {
        return repository.getMediaListings(param)
    }
}