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

package co.anitrend.support.crunchyroll.data.session.datasource.local.transformer

import co.anitrend.arch.data.transformer.ISupportTransformer
import co.anitrend.support.crunchyroll.data.session.entity.CrunchySessionCoreEntity
import co.anitrend.support.crunchyroll.data.session.model.CrunchySessionCoreModel

internal object CoreSessionEntityTransformer :
    ISupportTransformer<CrunchySessionCoreModel, CrunchySessionCoreEntity> {

    /**
     * Transforms the the [source] to the target type
     */
    override fun transform(source: CrunchySessionCoreModel): CrunchySessionCoreEntity {
        return CrunchySessionCoreEntity(
            sessionId = source.session_id,
            deviceType = source.device_type,
            deviceId = source.device_id
        )
    }
}