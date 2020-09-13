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

package co.anitrend.support.crunchyroll.feature.news

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.copper.flow.mapToList
import app.cash.copper.flow.observeQuery
import app.cash.turbine.test
import co.anitrend.support.crunchyroll.domain.news.entities.CrunchyNews
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.ExperimentalTime

@RunWith(AndroidJUnit4::class)
class NewContentProviderTest {

    private val context by lazy {
        InstrumentationRegistry.getInstrumentation().context
    }

    @ExperimentalTime
    @Test
    fun testAllDataQuery() {
        val query = Uri.parse("content://co.anitrend.crunchy.provider.news/feed/*")
        val flow = context.contentResolver
            .observeQuery(query)
            .mapToList { cursor ->
                CrunchyNews(
                    id = cursor.getString(0).hashCode().toLong(),
                    guid = cursor.getString(0),
                    title = cursor.getString(1),
                    image = cursor.getString(2),
                    author = cursor.getString(3),
                    subTitle = cursor.getString(4),
                    description = cursor.getString(5),
                    content = cursor.getString(6),
                    publishedOn = cursor.getLong(7)
                )
            }
        runBlocking {
            flow.test {
                val names = expectItem()
                assertNotNull(names)
                assertTrue(names.isNotEmpty())
            }
        }
    }
}