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

package co.anitrend.support.crunchyroll.data.session.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.session.entity.common.ICrunchySessionCoreEntity
import co.anitrend.support.crunchyroll.data.session.entity.common.ICrunchySessionEntity
import co.anitrend.support.crunchyroll.data.user.entity.CrunchyUserEntity

@Entity(
    indices = [
        Index(value = ["authenticationKey"], unique = true)
    ]
)
data class CrunchySessionEntity(
    @Embedded
    override val authenticatedUser: CrunchyUserEntity,
    override val authenticationKey: String,
    override val expiresAt: Long,
    @PrimaryKey
    override val sessionId: String,
    override val deviceType: String,
    override val deviceId: String
) : ICrunchySessionEntity, ICrunchySessionCoreEntity