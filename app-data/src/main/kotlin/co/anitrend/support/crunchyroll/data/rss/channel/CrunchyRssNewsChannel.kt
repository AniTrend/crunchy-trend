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

package co.anitrend.support.crunchyroll.data.rss.channel

import co.anitrend.support.crunchyroll.data.arch.RCF822Date
import co.anitrend.support.crunchyroll.data.news.model.CrunchyNewsModel
import co.anitrend.support.crunchyroll.data.rss.contract.ICrunchyRssChannel
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(strict = false)
internal data class CrunchyRssNewsChannel(
    @field:Element(required = false)
    override var title: String? = null,
    @field:Element(required = false)
    override var description: String? = null,
    @field:Element(required = false)
    override var copyright: String,
    @field:Element(name = "pubDate")
    override var publishedDate: RCF822Date,
    @field:Element
    override var language: String,
    @field:ElementList(
        name = "item",
        required = false,
        inline = true
    )
    override var item: List<CrunchyNewsModel>? = null
) : ICrunchyRssChannel<CrunchyNewsModel> {
    constructor() : this(
        copyright = "",
        publishedDate = "",
        language = ""
    )
}