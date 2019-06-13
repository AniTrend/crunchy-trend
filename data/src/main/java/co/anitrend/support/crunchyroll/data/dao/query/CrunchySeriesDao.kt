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

package co.anitrend.support.crunchyroll.data.dao.query

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.support.crunchyroll.data.model.series.CrunchySeries
import io.wax911.support.data.dao.ISupportQuery

@Dao
abstract class CrunchySeriesDao : ISupportQuery<CrunchySeries> {

    @Query("delete from CrunchySeries")
    abstract suspend fun clearTable()


    @Query("select * from CrunchySeries where series_id = :seriesId")
    abstract suspend fun findBySerieId(
        seriesId: Int
    ): CrunchySeries?

    @Query("select * from CrunchySeries where series_id = :seriesId")
    abstract fun findBySerieIdX(
        seriesId: Int
    ): LiveData<CrunchySeries?>



    @Query("select * from CrunchySeries where name match :seriesName order by name desc")
    abstract suspend fun findBySeriesName(
        seriesName: String
    ): List<CrunchySeries>

    @Query("select * from CrunchySeries where name match :seriesName order by name desc")
    abstract suspend fun findBySeriesNameX(
        seriesName: String
    ): LiveData<List<CrunchySeries>>

    @Query("select * from CrunchySeries where name match :seriesName order by name desc")
    abstract fun findBySeriesNameFactory(
        seriesName: String
    ): DataSource.Factory<Int, CrunchySeries>



    @Query("select * from CrunchySeries order by name desc")
    abstract suspend fun findAll(): List<CrunchySeries>

    @Query("select * from CrunchySeries order by name desc")
    abstract fun findAllX(): LiveData<List<CrunchySeries>>

    @Query("select * from CrunchySeries order by name desc")
    abstract fun findAllFactory(): DataSource.Factory<Int, CrunchySeries>
}