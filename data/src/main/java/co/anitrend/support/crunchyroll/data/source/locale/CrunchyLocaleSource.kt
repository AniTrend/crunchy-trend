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

import android.os.Bundle
import androidx.lifecycle.LiveData
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyCoreEndpoint
import co.anitrend.support.crunchyroll.data.arch.source.CrunchyWorkerDataSource
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import co.anitrend.support.crunchyroll.data.mapper.locale.CrunchyLocaleMapper
import co.anitrend.support.crunchyroll.data.model.core.CrunchyLocale
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.source.contract.ISourceObservable
import io.wax911.support.data.source.contract.ISupportDataSource
import kotlinx.coroutines.async
import org.koin.core.inject

class CrunchyLocaleSource(
    private val coreEndpoint: CrunchyCoreEndpoint
) : CrunchyWorkerDataSource() {

    override val databaseHelper by inject<CrunchyDatabase>()

    /**
     * Handles the requesting data from a the network source and informs the
     * network state that it is in the loading state
     *
     * @param bundle request parameters or more
     */
    override suspend fun startRequestForType(bundle: Bundle?): NetworkState {
        val futureResponse = async {
            coreEndpoint.fetchLocales()
        }

        val mapper = CrunchyLocaleMapper(
            parentJob = supervisorJob,
            localeDao = databaseHelper.crunchyLocaleDao()
        )

        return mapper.handleResponse(futureResponse)
    }

    /**
     * Clears all the data in a database table which will assure that
     * and refresh the backing storage medium with new network data
     *
     * @param bundle the request request parameters to use
     */
    override suspend fun refreshOrInvalidate(bundle: Bundle?): NetworkState {
        databaseHelper.crunchyLocaleDao().clearAll()
        return startRequestForType(bundle)
    }

    val locale = object : ISourceObservable<List<CrunchyLocale>> {

        /**
         * Returns the appropriate observable which we will monitor for updates,
         * common implementation may include but not limited to returning
         * data source live data for a database
         *
         * @param bundle request params, implementation is up to the developer
         */
        override fun observerOnLiveDataWith(bundle: Bundle): LiveData<List<CrunchyLocale>> {
            return databaseHelper.crunchyLocaleDao().getAllX()
        }
    }
}