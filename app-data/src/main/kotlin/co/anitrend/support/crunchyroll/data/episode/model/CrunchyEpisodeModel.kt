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

package co.anitrend.support.crunchyroll.data.episode.model

import co.anitrend.arch.extension.ext.empty
import co.anitrend.support.crunchyroll.data.arch.RCF822Date
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
internal data class CrunchyEpisodeModel @JvmOverloads constructor(
    @field:Element(name = "title")
    var title: String = String.empty(),
    @field:Element(name = "guid")
    var guid: String = String.empty(),
    @field:Element(name = "description", required = false)
    var description: String? = null,
    @field:Element(name = "mediaId")
    var mediaId: Int = 0,
    @field:Element(name = "premiumPubDate")
    var premiumAvailableDate: RCF822Date = String.empty(),
    @field:Element(name = "freePubDate")
    var freeAvailableDate: RCF822Date = String.empty(),
    @field:Element(name = "seriesTitle", required = false)
    var seriesTitle: String = String.empty(),
    @field:Element(name = "episodeTitle", required = false)
    var episodeTitle: String? = null,
    @field:Element(name = "episodeNumber", required = false)
    var episodeNumber: String? = null,
    @field:Element(name = "publisher", required = false)
    var publisher: String? = null,
    @field:Element(name = "season", required = false)
    var season: String? = null,
    @field:Element(name = "duration", required = false)
    var duration: Int? = null,
    @field:Element(name = "subtitleLanguages", required = false)
    var subtitleLanguages: String? = null,
    @field:Element(name = "restriction", required = false)
    var restriction: RestrictionModel? = null,
    @field:Element(name = "keywords", required = false)
    var keywords: String? = null,
    @field:Element(name = "rating", required = false)
    var rating: String? = null,
    @field:ElementList(name = "thumbnail", inline = true, required = false)
    var thumbnails : List<ThumbnailModel>? = null,
) {

    @Root(name = "thumbnail", strict = false)
    data class ThumbnailModel @JvmOverloads constructor(
        @field:Attribute(name = "url")
        var url: String = String.empty(),
        @field:Attribute(name = "width")
        var width: Int = 0,
        @field:Attribute(name = "height")
        var height: Int = 0
    )

    @Root(name = "restriction", strict = false)
    data class RestrictionModel @JvmOverloads constructor(
        @field:Attribute(name = "relationship")
        var relationship: String = String.empty(),
        @field:Attribute(name = "type")
        var type: String = String.empty(),
        @field:Element(name = "elements", required = false)
        var elements: String = String.empty()
    )
}