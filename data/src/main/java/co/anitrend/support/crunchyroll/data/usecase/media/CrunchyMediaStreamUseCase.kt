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

package co.anitrend.support.crunchyroll.data.usecase.media

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import co.anitrend.support.crunchyroll.data.api.endpoint.json.CrunchyMediaEndpoint
import co.anitrend.support.crunchyroll.data.dao.query.api.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.model.media.CrunchyMedia
import co.anitrend.support.crunchyroll.data.model.stream.CrunchyStreamData
import co.anitrend.support.crunchyroll.data.model.stream.CrunchyStreamInfo
import co.anitrend.support.crunchyroll.data.source.media.CrunchyMediaListDataSource
import co.anitrend.support.crunchyroll.data.source.media.CrunchyMediaStreamDataSource
import co.anitrend.support.crunchyroll.data.usecase.media.contract.IMediaUseCasePayload
import io.wax911.support.data.model.NetworkState
import io.wax911.support.data.model.UiModel
import io.wax911.support.data.usecase.core.ISupportCoreUseCase
import kotlinx.android.parcel.Parcelize

class CrunchyMediaStreamUseCase(
    private val mediaEndpoint: CrunchyMediaEndpoint
) : ISupportCoreUseCase<CrunchyMediaStreamUseCase.Payload, UiModel<CrunchyStreamInfo?>> {

    /**
     * Solves a given use case in the implementation target
     *
     * @param param input for solving a given use case
     */
    override fun invoke(param: Payload): UiModel<CrunchyStreamInfo?> {
        val dataSource = CrunchyMediaStreamDataSource(
            mediaEndpoint = mediaEndpoint,
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
            model = dataSource.streamInfo(null),
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
        override val mediaId: Int
    ) : IMediaUseCasePayload, Parcelable
}