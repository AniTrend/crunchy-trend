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

package co.anitrend.support.crunchyroll.data.authentication.mapper

import co.anitrend.support.crunchyroll.data.arch.mapper.DefaultMapper
import co.anitrend.support.crunchyroll.data.arch.model.CrunchyContainer
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.authentication.datasource.local.transformer.LoginEntityTransformer
import co.anitrend.support.crunchyroll.data.authentication.entity.CrunchyLoginEntity
import co.anitrend.support.crunchyroll.data.authentication.model.CrunchyLoginModel

internal class LoginResponseMapper(
    private val dao: CrunchyLoginDao
) : DefaultMapper<CrunchyContainer<CrunchyLoginModel>, CrunchyLoginEntity?>() {

    /**
     * Save [data] into your desired local source
     */
    override suspend fun persist(data: CrunchyLoginEntity?) {
        if (data != null) {
            dao.clearTable()
            dao.upsert(data)
        }
    }

    /**
     * Creates mapped objects and handles the database operations which may be required to map various objects,
     * called in [retrofit2.Callback.onResponse] after assuring that the response was a success
     *
     * @param source the incoming data source type
     * @return Mapped object that will be consumed by [onResponseDatabaseInsert]
     * @see [ISupportResponseHelper.invoke]
     */
    override suspend fun onResponseMapFrom(
        source: CrunchyContainer<CrunchyLoginModel>
    ): CrunchyLoginEntity? {
        return source.data?.let(LoginEntityTransformer::transform)
    }
}