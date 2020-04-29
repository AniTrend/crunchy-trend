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

import co.anitrend.arch.extension.empty
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "restriction", strict = false)
internal data class CrunchyEpisodeRestrictionModel(
    @field:Attribute(
        required = false
    )
    var relationship: String,
    @field:Attribute(
        required = false
    )
    var type: String,
    @field:Element(
        required = false
    )
    var elements: String
) {
    constructor(): this (
        String.empty(),
        String.empty(),
        String.empty()
    )

    /**
     * Returns a hash code value for the object.  The general contract of `hashCode` is:
     *
     * * Whenever it is invoked on the same object more than once, the `hashCode` method must consistently return the same integer, provided no information used in `equals` comparisons on the object is modified.
     * * If two objects are equal according to the `equals()` method, then calling the `hashCode` method on each of the two objects must produce the same integer result.
     */
    override fun hashCode(): Int {
        var result = relationship.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + elements.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CrunchyEpisodeRestrictionModel

        if (relationship != other.relationship) return false
        if (type != other.type) return false
        if (elements != other.elements) return false

        return true
    }
}