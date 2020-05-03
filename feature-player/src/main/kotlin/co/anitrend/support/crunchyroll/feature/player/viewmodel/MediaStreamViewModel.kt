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

package co.anitrend.support.crunchyroll.feature.player.viewmodel

import androidx.lifecycle.ViewModel
import co.anitrend.arch.core.viewmodel.SupportViewModel
import co.anitrend.support.crunchyroll.data.stream.usecase.MediaStreamUseCaseType
import co.anitrend.support.crunchyroll.domain.stream.models.CrunchyMediaStreamQuery
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream
import co.anitrend.support.crunchyroll.feature.player.viewmodel.model.MediaStreamModelState

class MediaStreamViewModel(
    useCase: MediaStreamUseCaseType
) : ViewModel() {
    val state = MediaStreamModelState(useCase)

    override fun onCleared() {
        state.onCleared()
        super.onCleared()
    }
}