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

package co.anitrend.support.crunchyroll.data.repository.collection

import android.os.Bundle
import androidx.paging.PagedList
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyCollectionEndpoint
import co.anitrend.support.crunchyroll.data.model.collection.CrunchyCollection
import io.wax911.support.data.model.UiModel
import io.wax911.support.data.repository.SupportRepository

class CrunchyCollectionRepository(
    private val crunchyCollectionEndpoint: CrunchyCollectionEndpoint
) : SupportRepository<PagedList<CrunchyCollection>>() {

    /**
     * Handles dispatching of network requests to a background thread
     *
     * @param bundle bundle of parameters for the request
     */
    override fun invokeRequest(bundle: Bundle): UiModel<PagedList<CrunchyCollection>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}