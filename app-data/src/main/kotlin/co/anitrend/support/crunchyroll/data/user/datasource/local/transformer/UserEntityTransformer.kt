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

package co.anitrend.support.crunchyroll.data.user.datasource.local.transformer

import co.anitrend.arch.data.transformer.ISupportTransformer
import co.anitrend.support.crunchyroll.data.user.entity.CrunchyUserEntity
import co.anitrend.support.crunchyroll.data.user.model.CrunchyUserModel
import co.anitrend.support.crunchyroll.domain.user.enums.CrunchyAccessType

internal object UserEntityTransformer : ISupportTransformer<CrunchyUserModel, CrunchyUserEntity> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyUserModel): CrunchyUserEntity {
        return CrunchyUserEntity(
            userId = source.user_id.toLong(),
            username = source.username,
            email = source.email,
            premium = source.premium,
            accessType = source.access_type?.let {
                CrunchyAccessType.valueOf(it)
            }
        )
    }
}