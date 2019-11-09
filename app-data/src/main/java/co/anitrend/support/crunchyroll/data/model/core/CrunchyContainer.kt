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

package co.anitrend.support.crunchyroll.data.model.core

import co.anitrend.support.crunchyroll.data.arch.ResponseStatus
import co.anitrend.support.crunchyroll.data.arch.ResponseStatusContract

data class CrunchyContainer<D>(
    val code: ResponseStatus,
    val error: Boolean,
    val data: D?,
    val message: String?
) {
    fun isBadSession() =
        code == ResponseStatusContract.BAD_SESSION

    fun isBadRequest() =
        code == ResponseStatusContract.BAD_REQUEST

    fun isObjectNotFound() =
        code == ResponseStatusContract.OBJECT_NOT_FOUND

    fun isForbidden() =
        code == ResponseStatusContract.FORBIDDEN
}
