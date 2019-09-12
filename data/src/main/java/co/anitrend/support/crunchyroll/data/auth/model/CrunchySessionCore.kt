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

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import co.anitrend.support.crunchyroll.data.auth.model.contract.ICrunchySession

@Entity(
    indices = [
        Index(value = ["session_id"], unique = true)
    ]
)
data class CrunchySessionCore(
    @PrimaryKey(autoGenerate = true)
    override val sessionCoreId: Int = 1,
    override val session_id: String,
    override val country_code: String,
    override val device_type: String,
    override val device_id: String,
    override val version: String
) : ICrunchySession