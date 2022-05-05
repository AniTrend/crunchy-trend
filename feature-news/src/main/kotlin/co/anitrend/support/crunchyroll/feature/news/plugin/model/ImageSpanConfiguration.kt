/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.feature.news.plugin.model

import co.anitrend.support.crunchyroll.feature.news.plugin.model.contract.IPhotoSpan
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImageSize

internal data class ImageSpanConfiguration(
    val configuration: MarkwonConfiguration,
    val renderProps: RenderProps,
    val tag: HtmlTag,
    val cutoffImageSize: Int,
    override val magnificationScale: Float,
    override val sizeMeasurementUnit: SizeMeasurementUnit,
    override val isClickable: Boolean
) : IPhotoSpan {

    override fun addPropertiesToImage(source: String, imageSize: ImageSize) {
        ImageProps.DESTINATION.set(renderProps, source)
        ImageProps.IMAGE_SIZE.set(renderProps, imageSize)
        // don't know what this does yet, no documentation provided
        ImageProps.REPLACEMENT_TEXT_IS_LINK.set(renderProps, isClickable)
    }

    companion object {
        private const val TITLE_ATTR = "title"
        private const val ALT_ATTR = "alt"
    }
}