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

package co.anitrend.support.crunchyroll.data.authentication.datasource.local.transformer

import co.anitrend.arch.data.transformer.ISupportTransformer
import co.anitrend.support.crunchyroll.data.authentication.entity.CrunchyLoginEntity
import co.anitrend.support.crunchyroll.data.authentication.model.CrunchyLoginModel
import co.anitrend.support.crunchyroll.data.user.datasource.local.transformer.UserEntityTransformer
import co.anitrend.support.crunchyroll.data.util.extension.iso8601ToUnixTime

internal object LoginEntityTransformer : ISupportTransformer<CrunchyLoginModel, CrunchyLoginEntity> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchyLoginModel): CrunchyLoginEntity {
        val userEntity = UserEntityTransformer.transform(source.user)
        return CrunchyLoginEntity(
            userId = userEntity.userId,
            username = userEntity.username,
            email = userEntity.email,
            premium = userEntity.premium,
            accessType = userEntity.accessType,
            auth = source.auth,
            expiresAt = source.expires.iso8601ToUnixTime()
        )
    }
}