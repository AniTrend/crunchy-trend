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

import co.anitrend.support.crunchyroll.data.arch.RCF822Date
import co.anitrend.support.crunchyroll.data.rss.contract.IRssCopyright
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
internal data class CrunchyNewsModel(
    @field:Element(name = "title")
    var title: String,
    @field:Element(name = "author")
    var author: String,
    @field:Element(name = "description")
    var description: String,
    @field:Element(name = "encoded")
    var encoded: String,
    @field:Element(name = "pubDate")
    var publishedOn: RCF822Date,
    @field:Element(name = "link")
    var referenceLink: String,
    @field:Element(name = "guid")
    var guid: String,
    override var copyright: String,
    var publishedTime: Long
) : IRssCopyright {

    constructor():
            this(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                0
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
            is CrunchyNewsModel -> other.guid == guid
            else -> super.equals(other)
        }
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + encoded.hashCode()
        result = 31 * result + publishedOn.hashCode()
        result = 31 * result + referenceLink.hashCode()
        result = 31 * result + guid.hashCode()
        result = 31 * result + copyright.hashCode()
        return result
    }
}