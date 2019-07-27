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

package co.anitrend.support.crunchyroll.core.viewmodel.media

import androidx.paging.PagedList
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaInfoUseCase
import co.anitrend.support.crunchyroll.data.usecase.media.CrunchyMediaListUseCase
import io.wax911.support.core.viewmodel.SupportViewModel
import io.wax911.support.data.repository.contract.ISupportRepository

class CrunchyMediaListViewModel(
    repository: ISupportRepository<PagedList<CrunchyMedia>, CrunchyMediaListUseCase.Payload>
) : SupportViewModel<PagedList<CrunchyMedia>, CrunchyMediaListUseCase.Payload>(repository)