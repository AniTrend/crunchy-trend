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

package co.anitrend.support.crunchyroll.data.source.locale

import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyCoreEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchyLocaleDao
import co.anitrend.support.crunchyroll.data.mapper.locale.CrunchyLocaleMapper
import co.anitrend.support.crunchyroll.data.model.core.CrunchyLocale
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.source.coroutine.SupportCoroutineDataSource
import kotlinx.coroutines.async

class CrunchyLocaleSource(
    private val coreEndpoint: CrunchyCoreEndpoint,
    private val localeDao: CrunchyLocaleDao
) : SupportCoroutineDataSource() {

    /**
     * Handles the requesting data from a the network source and return
     * [NetworkState] to the caller after execution.
     *
     * In this context the super.invoke() method will allow a retry action to be set
     */
    override suspend fun invoke(): NetworkState {
        super.invoke()
        val futureResponse = async {
            coreEndpoint.fetchLocales()
        }

        val mapper = CrunchyLocaleMapper(
            parentJob = supervisorJob,
            localeDao = localeDao
        )

        return mapper.handleResponse(futureResponse)
    }

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        localeDao.clearTable()
    }

    val locale =
        object : ISourceObservable<List<CrunchyLocale>, Nothing?> {

        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param parameter parameters, implementation is up to the developer
         */
        override fun invoke(parameter: Nothing?): LiveData<List<CrunchyLocale>> {
            return localeDao.getAllX()
        }
    }
}
