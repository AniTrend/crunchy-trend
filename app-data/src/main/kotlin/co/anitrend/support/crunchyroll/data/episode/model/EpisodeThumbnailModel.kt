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
import org.simpleframework.xml.Root

@Root(name = "thumbnail", strict = false)
internal data class EpisodeThumbnailModel(
    @field:Attribute(
        required = false
    )
    var url: String,
    @field:Attribute(
        required = false
    )
    var width: Int,
    @field:Attribute(
        required = false
    )
    var height: Int
) {
    constructor() : this(
        url = String.empty(),
        height = 0,
        width = 0
    )

    override fun equals(other: Any?): Boolean {
        when (other) {
            is EpisodeThumbnailModel -> {
                return other.url == url
            }
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}