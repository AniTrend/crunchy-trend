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

package co.anitrend.support.crunchyroll.data.usecase.rss

import android.os.Parcelable
import androidx.annotation.StringDef
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyEndpoint
import co.anitrend.support.crunchyroll.data.api.endpoint.xml.CrunchyFeedEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.rss.CrunchyRssMediaDao
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssMedia
import co.anitrend.support.crunchyroll.data.source.rss.CrunchyRssMediaSource
import co.anitrend.support.crunchyroll.data.usecase.rss.contract.IRssUseCase
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.UiModel
import kotlinx.android.parcel.Parcelize

class CrunchyRssMediaUseCase(
    private val rssMediaDao: CrunchyRssMediaDao,
    private val rssCrunchyEndpoint: CrunchyEndpoint,
    private val rssFeedCrunchyEndpoint: CrunchyFeedEndpoint
    ) : IRssUseCase<CrunchyRssMediaUseCase.Payload, CrunchyRssMedia> {

    /**
     * Solves a given use case in the implementation target
     *
     * @param param input for solving a given use case
     */
    override fun invoke(param: Payload): UiModel<PagedList<CrunchyRssMedia>> {
        val dataSource = CrunchyRssMediaSource(
            rssMediaDao = rssMediaDao,
            rssCrunchyEndpoint = rssCrunchyEndpoint,
            rssFeedCrunchyEndpoint = rssFeedCrunchyEndpoint,
            payload = param
        )

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            MutableLiveData<NetworkState>()
        }

        return UiModel(
            model = dataSource.media(param),
            networkState = dataSource.networkState,
            refresh = {
                dataSource.invalidateAndRefresh()
                refreshTrigger.value = null
            },
            refreshState = refreshState,
            retry = {
                dataSource.retryRequest()
            }
        )
    }

    @Parcelize
    data class Payload(
        override val locale: String,
        val seriesSlug: String?,
        @RequestType
        val mediaRssRequestType: String
    ) : IRssUseCase.IRssPayload, Parcelable {
        @StringDef(
            RequestType.GET_BY_SERIES_SLUG,
            RequestType.GET_LATEST_FEED
        )
        annotation class RequestType {
            companion object {
                const val GET_BY_SERIES_SLUG = "GET_BY_SERIES_SLUG"
                const val GET_LATEST_FEED = "GET_LATEST_FEED"
            }
        }
    }
}