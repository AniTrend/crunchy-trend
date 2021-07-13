/*
 *    Copyright 2021 AniTrend
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

package co.anitrend.support.crunchyroll.data.episode.model.page

import co.anitrend.support.crunchyroll.data.episode.model.CrunchyEpisodeModel
import co.anitrend.support.crunchyroll.data.rss.contract.ICrunchyContainer
import co.anitrend.support.crunchyroll.data.rss.contract.ICrunchyRssChannel
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
internal data class CrunchyEpisodePageModel @JvmOverloads constructor(
    @field:Element(
        name = "channel",
        required = true
    )
    override var channel: ChannelModel? = null
) : ICrunchyContainer<CrunchyEpisodePageModel.ChannelModel> {

    @Root(name = "channel", strict = false)
    internal data class ChannelModel @JvmOverloads constructor(
        @field:ElementList(
            name = "item",
            required = false,
            inline = true
        )
        override var items: List<CrunchyEpisodeModel>? = null,
    ) : ICrunchyRssChannel<CrunchyEpisodeModel>
}