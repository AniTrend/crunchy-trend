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

package co.anitrend.support.crunchyroll.data.model.rss.core

import co.anitrend.support.crunchyroll.data.model.rss.channel.CrunchyRssNewsChannel
import co.anitrend.support.crunchyroll.data.model.rss.contract.ICrunchyContainer
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class CrunchyRssNewsContainer(
    @field:Element(
        name = "channel",
        required = false
    )
    override var channel: CrunchyRssNewsChannel? = null
) : ICrunchyContainer<CrunchyRssNewsChannel>{
    constructor(): this(
        channel = null
    )
}