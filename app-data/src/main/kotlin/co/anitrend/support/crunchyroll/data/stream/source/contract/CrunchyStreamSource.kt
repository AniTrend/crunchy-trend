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

package co.anitrend.support.crunchyroll.data.stream.source.contract

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.anitrend.arch.data.source.core.SupportCoreDataSource
import co.anitrend.support.crunchyroll.domain.stream.models.CrunchyMediaStreamQuery
import co.anitrend.support.crunchyroll.domain.stream.entities.MediaStream

abstract class CrunchyStreamSource : SupportCoreDataSource() {

    protected val observable = MutableLiveData<List<MediaStream>?>()
    abstract fun getMediaStream(query: CrunchyMediaStreamQuery): LiveData<List<MediaStream>?>

    /**
     * Clears data sources (databases, preferences, e.t.c)
     */
    override suspend fun clearDataSource() {
        observable.value = null
    }
}