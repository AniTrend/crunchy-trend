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

package co.anitrend.support.crunchyroll.data.auth.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import androidx.room.RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED
import co.anitrend.support.crunchyroll.data.arch.ISO8601Date
import co.anitrend.support.crunchyroll.data.auth.model.contract.ICrunchySessionUser
import co.anitrend.support.crunchyroll.data.model.user.CrunchyUser

@Entity
@SuppressWarnings(PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
data class CrunchyLogin(
    @PrimaryKey
    val loginUserId: Int,
    @Embedded
    override val user: CrunchyUser,
    override val auth: String,
    override val expires: ISO8601Date
) : ICrunchySessionUser