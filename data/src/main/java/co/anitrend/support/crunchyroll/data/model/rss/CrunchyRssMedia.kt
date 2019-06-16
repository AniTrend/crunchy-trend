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
import co.anitrend.support.crunchyroll.data.arch.CrunchyMediaType
import co.anitrend.support.crunchyroll.data.arch.RCF822Date
import co.anitrend.support.crunchyroll.data.model.rss.contract.IRssCopyright
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Entity
@Root(name = "item", strict = false)
data class CrunchyRssMedia(
    @Element
    val title: String,
    @Element(required = false)
    val description: String?,
    @Element(name = "category")
    val mediaType: CrunchyMediaType,
    @Element(name = "mediaId")
    @PrimaryKey
    val mediaId: Int,
    @Element(name = "premiumPubDate")
    val premiumAvailableDate: RCF822Date,
    @Element(name = "freePubDate")
    val freeAvailableDate: RCF822Date,
    @Element(name = "seriesTitle")
    val seriesTitle: String,
    @Element(name = "episodeTitle")
    val episodeTitle: String,
    @Element(name = "episodeNumber")
    val episodeNumber: Int,
    @Element(name = "duration")
    val duration: Int,
    @Element(
        name = "publisher",
        required = false
    )
    val publisher: String?,
    @ElementList(
        inline = true,
        required = false
    )
    val thumbnail : List<MediaThumbnail>?,
    override val copyright: String?
) : IRssCopyright {

    @Root(name = "thumbnail", strict = false)
    data class MediaThumbnail(
        @Element
        val url: String,
        @Element
        val width: Int,
        @Element
        val height: Int
    )

    /**
     * Indicates whether some other object is "equal to" this one. Implementations must fulfil the following
     * requirements:
     *
     * * Reflexive: for any non-null value `x`, `x.equals(x)` should return true.
     * * Symmetric: for any non-null values `x` and `y`, `x.equals(y)` should return true if and only if `y.equals(x)` returns true.
     * * Transitive:  for any non-null values `x`, `y`, and `z`, if `x.equals(y)` returns true and `y.equals(z)` returns true, then `x.equals(z)` should return true.
     * * Consistent:  for any non-null values `x` and `y`, multiple invocations of `x.equals(y)` consistently return true or consistently return false, provided no information used in `equals` comparisons on the objects is modified.
     * * Never equal to null: for any non-null value `x`, `x.equals(null)` should return false.
     *
     * Read more about [equality](https://kotlinlang.org/docs/reference/equality.html) in Kotlin.
     */
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is CrunchyRssMedia -> other.mediaId == mediaId
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + mediaId
        result = 31 * result + premiumAvailableDate.hashCode()
        result = 31 * result + freeAvailableDate.hashCode()
        result = 31 * result + seriesTitle.hashCode()
        result = 31 * result + episodeTitle.hashCode()
        result = 31 * result + episodeNumber
        result = 31 * result + duration
        result = 31 * result + (publisher?.hashCode() ?: 0)
        result = 31 * result + (thumbnail?.hashCode() ?: 0)
        result = 31 * result + (copyright?.hashCode() ?: 0)
        return result
    }
}