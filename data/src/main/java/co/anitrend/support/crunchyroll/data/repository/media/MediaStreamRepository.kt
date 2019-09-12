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

package co.anitrend.support.crunchyroll.data.repository.media

import co.anitrend.arch.data.model.UserInterfaceState
import co.anitrend.arch.data.repository.SupportRepository
import co.anitrend.support.crunchyroll.data.datasource.auto.media.contract.MediaStreamSource
import co.anitrend.support.crunchyroll.domain.entities.query.media.MediaStreamQuery
import co.anitrend.support.crunchyroll.domain.entities.result.media.MediaStream
import co.anitrend.support.crunchyroll.domain.repositories.media.IStreamRepository

class MediaStreamRepository(
    private val source: MediaStreamSource
) : SupportRepository(source), IStreamRepository<UserInterfaceState<List<MediaStream>?>> {

    override fun getStream(query: MediaStreamQuery) =
        UserInterfaceState.create(
            model = source.getMediaStream(query),
            source = source
        )
}