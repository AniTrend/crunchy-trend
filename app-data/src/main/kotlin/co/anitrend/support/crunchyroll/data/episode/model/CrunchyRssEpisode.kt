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

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.anitrend.arch.extension.empty
import co.anitrend.support.crunchyroll.data.arch.RCF822Date
import co.anitrend.support.crunchyroll.data.rss.contract.IRssCopyright
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Entity
@Root(name = "item", strict = false)
data class CrunchyRssEpisode(
    @field:Element(
        name = "title",
        required = false
    )
    var title: String,
    @field:Element(
        name = "description",
        required = false
    )
    var description: String? = null,
    @field:Element(
        name = "mediaId",
        required = false
    )
    @PrimaryKey
    var mediaId: Int,
    @field:Element(
        name = "premiumPubDate",
        required = false
    )
    var premiumAvailableDate: RCF822Date,
    @field:Element(
        name = "freePubDate",
        required = false
    )
    var freeAvailableDate: RCF822Date,
    @field:Element(
        required = false
    )
    var seriesTitle: String,
    @field:Element(
        required = false
    )
    var episodeTitle: String,
    @field:Element(
        required = false
    )
    var episodeNumber: String? = null,
    @field:Element(
        required = false
    )
    var duration: Int? = null,
    @field:Element(
        required = false
    )
    var publisher: String? = null,
    @field:Element(
        required = false
    )
    var subtitleLanguages: String? = null,
    @field:Element(
        required = false
    )
    var restriction: CrunchyEpisodeRestriction? = null,
    @field:ElementList(
        inline = true,
        required = false
    )
    var thumbnail : List<EpisodeThumbnail>?,
    override var copyright: String = String.empty(),
    var freeAvailableTime: Long = 0,
    var premiumAvailableTime: Long = 0
) : IRssCopyright {

    constructor() : this(
        title = String.empty(),
        mediaId = 0,
        premiumAvailableDate = String.empty(),
        freeAvailableDate = String.empty(),
        seriesTitle = String.empty(),
        episodeTitle = String.empty(),
        thumbnail = null
    )

    /**
     * Indicates whether some other object is "equal to" this one. Implementations must fulfil the following
     * requirements:
     *
     * * Reflexive: for any non-null varue `x`, `x.equals(x)` should return true.
     * * Symmetric: for any non-null varues `x` and `y`, `x.equals(y)` should return true if and only if `y.equals(x)` returns true.
     * * Transitive:  for any non-null varues `x`, `y`, and `z`, if `x.equals(y)` returns true and `y.equals(z)` returns true, then `x.equals(z)` should return true.
     * * Consistent:  for any non-null varues `x` and `y`, multiple invocations of `x.equals(y)` consistently return true or consistently return false, provided no information used in `equals` comparisons on the objects is modified.
     * * Never equal to null: for any non-null varue `x`, `x.equals(null)` should return false.
     *
     * Read more about [equality](https://kotlinlang.org/docs/reference/equality.html) in Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is CrunchyRssEpisode -> other.mediaId == mediaId
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + mediaId
        result = 31 * result + premiumAvailableDate.hashCode()
        result = 31 * result + freeAvailableDate.hashCode()
        result = 31 * result + seriesTitle.hashCode()
        result = 31 * result + episodeTitle.hashCode()
        result = 31 * result + (episodeNumber?.hashCode() ?: 0)
        result = 31 * result + (duration?.hashCode() ?: 0)
        result = 31 * result + (publisher?.hashCode() ?: 0)
        result = 31 * result + (subtitleLanguages?.hashCode() ?: 0)
        result = 31 * result + (restriction?.hashCode() ?: 0)
        result = 31 * result + (thumbnail?.hashCode() ?: 0)
        result = 31 * result + copyright.hashCode()
        return result
    }
}