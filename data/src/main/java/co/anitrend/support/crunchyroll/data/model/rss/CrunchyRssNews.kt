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

package co.anitrend.support.crunchyroll.data.model.rss

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Entity
@Root(name = "item", strict = false)
data class CrunchyRssNews(
    @Element
    val title: String,
    @Element
    val author: String,
    @Element
    val description: String,
    @Element
    val encoded: String,
    @Element(name = "pubDate")
    val publishedOn: String,
    @Element(name = "link")
    val referenceLink: String,
    @Element
    @PrimaryKey
    val guid: String
)