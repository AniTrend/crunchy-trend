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

package co.anitrend.support.crunchyroll.data.source.series

import android.os.Bundle
import co.anitrend.support.crunchyroll.data.api.endpoint.CrunchySeriesEndpoint
import co.anitrend.support.crunchyroll.data.dao.CrunchyDatabase
import io.wax911.support.data.source.SupportDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.inject

class CrunchySeriesDataSource(
    parentCoroutineJob: Job? = null,
    private val seriesEndpoint: CrunchySeriesEndpoint
) : SupportDataSource(parentCoroutineJob) {

    override val databaseHelper by inject<CrunchyDatabase>()

    /**
     * Handles the requesting data from a the network source and informs the
     * network state that it is in the loading state
     *
     * @param bundle request parameters or more
     */
    override fun startRequestForType(bundle: Bundle) {
        super.startRequestForType(bundle)
    }

    /**
     * Clears all the data in a database table which will assure that
     * and refresh the backing storage medium with new network data
     */
    override fun refreshOrInvalidate() {
        launch {
            databaseHelper.crunchySeriesDao().clearTable()
        }
        super.refreshOrInvalidate()
    }
}