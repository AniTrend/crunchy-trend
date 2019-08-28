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

package co.anitrend.support.crunchyroll.data.mapper.rss

import androidx.paging.PagingRequestHelper
import co.anitrend.support.crunchyroll.data.arch.mapper.CrunchyRssMapper
import co.anitrend.support.crunchyroll.data.dao.query.rss.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssNews
import co.anitrend.support.crunchyroll.data.model.rss.contract.ICrunchyRssChannel
import co.anitrend.support.crunchyroll.data.util.CrunchyDateHelper
import co.anitrend.support.crunchyroll.data.util.extension.rcf822ToUnixTime
import io.wax911.support.extension.util.contract.ISupportDateHelper
import kotlinx.coroutines.Job
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class CrunchyRssNewsMapper(
    parentJob: Job,
    private val crunchyRssNewsDao: CrunchyRssNewsDao,
    pagingRequestHelper: PagingRequestHelper.Request.Callback
) : CrunchyRssMapper<CrunchyRssNews>(parentJob, pagingRequestHelper), KoinComponent {

    private val dateHelper by inject<ISupportDateHelper>()

    override suspend fun onResponseMapFrom(source: ICrunchyRssChannel<CrunchyRssNews>): List<CrunchyRssNews> {
        return source.item?.map {
            it.copy(
                copyright = source.copyright,
                publishedTime = it.publishedOn.rcf822ToUnixTime(dateHelper) ?: 0
            )
        } ?: emptyList()
    }

    /**
     * Inserts the given object into the implemented room database,
     * called in [retrofit2.Callback.onResponse]
     * @see [handleResponse]
     *
     * @param mappedData mapped object from [onResponseMapFrom] to insert into the database
     */
    override suspend fun onResponseDatabaseInsert(mappedData: List<CrunchyRssNews>) {
        if (mappedData.isNotEmpty())
            crunchyRssNewsDao.upsert(mappedData)
        else
            Timber.tag(moduleTag).i("mapped data was empty")
    }
}