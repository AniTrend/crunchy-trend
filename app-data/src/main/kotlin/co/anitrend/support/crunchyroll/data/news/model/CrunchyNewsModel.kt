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

package co.anitrend.support.crunchyroll.data.news.model

import co.anitrend.arch.extension.ext.empty
import co.anitrend.support.crunchyroll.data.arch.RCF822Date
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
internal data class CrunchyNewsModel @JvmOverloads constructor(
    @field:Element(name = "title")
    var title: String = String.empty(),
    @field:Element(name = "author", required = false)
    var author: String = String.empty(),
    @field:Element(name = "description")
    var description: String = String.empty(),
    @field:Element(name = "encoded")
    var contentEncoded: String = String.empty(),
    @field:Element(name = "pubDate")
    var publishedOn: RCF822Date = String.empty(),
    @field:Element(name = "link")
    var referenceLink: String = String.empty()
)