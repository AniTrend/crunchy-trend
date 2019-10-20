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

package co.anitrend.support.crunchyroll.data.datasource.local.api

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import co.anitrend.arch.data.dao.ISupportQuery
import co.anitrend.support.crunchyroll.data.model.series.CrunchySeries

@Dao
interface CrunchySeriesDao : ISupportQuery<CrunchySeries> {

    @Query("delete from CrunchySeries")
    suspend fun clearTable()


    @Query("select *, rowid from CrunchySeries where series_id = :seriesId")
    suspend fun findBySeriesId(
        seriesId: Int
    ): CrunchySeries?

    @Query("select *, rowid from CrunchySeries where series_id = :seriesId")
    fun findBySeriesIdX(
        seriesId: Int
    ): LiveData<CrunchySeries?>



    @Query("select *, rowid from CrunchySeries where name match :seriesName order by name desc")
    suspend fun findBySeriesName(
        seriesName: String
    ): List<CrunchySeries>

    @Query("select *, rowid from CrunchySeries where name match :seriesName order by name desc")
    fun findBySeriesNameX(
        seriesName: String
    ): LiveData<List<CrunchySeries>>

    @Query("select *, rowid from CrunchySeries where name match :seriesName order by name desc")
    fun findBySeriesNameFactory(
        seriesName: String
    ): DataSource.Factory<Int, CrunchySeries>



    @Query("select *, rowid from CrunchySeries order by name desc")
    suspend fun findAll(): List<CrunchySeries>

    @Query("select *, rowid from CrunchySeries order by name desc")
    fun findAllX(): LiveData<List<CrunchySeries>>

    @Query("select *, rowid from CrunchySeries order by name desc")
    fun findAllFactory(): DataSource.Factory<Int, CrunchySeries>
}