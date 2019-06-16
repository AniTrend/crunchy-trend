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

package co.anitrend.support.crunchyroll.data.usecase.rss.contract

import androidx.paging.PagedList
import co.anitrend.support.crunchyroll.data.model.rss.contract.IRssCopyright
import co.anitrend.support.crunchyroll.data.usecase.contract.IMappable
import io.wax911.support.data.model.UiModel
import io.wax911.support.data.usecase.core.ISupportCoreUseCase

interface IRssUseCase<P : IRssUseCase.IRssPayload, R : IRssCopyright> :
    ISupportCoreUseCase<P, UiModel<PagedList<R>>>{

    interface IRssPayload : IMappable {
        val locale: String

        override fun toMap() = mapOf(
            "lang" to locale
        )
    }
}