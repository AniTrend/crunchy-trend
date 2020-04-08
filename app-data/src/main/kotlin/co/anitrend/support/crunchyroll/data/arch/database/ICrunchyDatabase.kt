/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.data.arch.database

import co.anitrend.support.crunchyroll.data.authentication.datasource.local.CrunchyLoginDao
import co.anitrend.support.crunchyroll.data.catalog.datasource.local.CrunchyCatalogDao
import co.anitrend.support.crunchyroll.data.collection.datasource.local.CrunchyCollectionDao
import co.anitrend.support.crunchyroll.data.episode.datasource.local.CrunchyRssEpisodeDao
import co.anitrend.support.crunchyroll.data.locale.datasource.local.CrunchyLocaleDao
import co.anitrend.support.crunchyroll.data.media.datasource.local.CrunchyMediaDao
import co.anitrend.support.crunchyroll.data.news.datasource.local.CrunchyRssNewsDao
import co.anitrend.support.crunchyroll.data.series.datasource.local.CrunchySeriesDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionCoreDao
import co.anitrend.support.crunchyroll.data.session.datasource.local.CrunchySessionDao

interface ICrunchyDatabase {

    fun crunchyLocaleDao(): CrunchyLocaleDao

    fun crunchySessionCoreDao(): CrunchySessionCoreDao

    fun crunchyCollectionDao(): CrunchyCollectionDao
    fun crunchyMediaDao(): CrunchyMediaDao
    fun crunchySeriesDao(): CrunchySeriesDao

    fun crunchyLoginDao(): CrunchyLoginDao
    fun crunchySessionDao(): CrunchySessionDao

    fun crunchyRssNewsDao(): CrunchyRssNewsDao
    fun crunchyRssMediaDao(): CrunchyRssEpisodeDao

    fun crunchyCatalogDao(): CrunchyCatalogDao
}