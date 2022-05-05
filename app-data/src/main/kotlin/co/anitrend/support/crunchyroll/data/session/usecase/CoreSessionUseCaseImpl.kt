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

package co.anitrend.support.crunchyroll.data.session.usecase

import co.anitrend.arch.data.repository.contract.ISupportRepository
import co.anitrend.support.crunchyroll.domain.session.interactors.CoreSessionUseCase
import co.anitrend.support.crunchyroll.domain.session.repositories.ISessionRepository

internal class CoreSessionUseCaseImpl(
    repository: ISessionRepository
) : CoreSessionUseCase(repository) {
    /**
     * Informs underlying repositories or related components running background operations to stop
     */
    override fun onCleared() {
        repository as ISupportRepository
        repository.onCleared()
    }
}