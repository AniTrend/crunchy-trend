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

package co.anitrend.support.crunchyroll.feature.news.plugin.spans

import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.core.CoreProps
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImageSize
import org.commonmark.node.Image
import org.commonmark.node.Link

internal class ImageTagHandler private constructor() : SimpleTagHandler() {

    override fun getSpans(
        configuration: MarkwonConfiguration,
        renderProps: RenderProps,
        tag: HtmlTag
    ): Any? {
        val attributes = tag.attributes()
        val imageDestination = attributes["src"]
        val height = attributes["height"]?.toFloat()
        val imageSize = ImageSize(
            null,
            height?.let { ImageSize.Dimension(it * 1.5f, "px") }
        )

        if (imageDestination == null)
            return null

        ImageProps.DESTINATION.set(renderProps, imageDestination)
        ImageProps.IMAGE_SIZE.set(renderProps, imageSize)
        ImageProps.REPLACEMENT_TEXT_IS_LINK.set(renderProps, false)

        val imageSpanFactory = configuration
            .spansFactory()
            .get(Image::class.java)

        return if (imageSize.height?.value != null && imageSize.height.value > 100){
            val linkSpanFactory = configuration
                .spansFactory()
                .get(Link::class.java)

            CoreProps.LINK_DESTINATION.set(renderProps, imageDestination)
            arrayOf(
                imageSpanFactory?.getSpans(configuration, renderProps),
                linkSpanFactory?.getSpans(configuration, renderProps)
            )
        } else arrayOf(imageSpanFactory?.getSpans(configuration, renderProps))
    }

    override fun supportedTags() = listOf("img")

    companion object {
        fun create() = ImageTagHandler()
    }
}