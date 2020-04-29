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

package co.anitrend.support.crunchyroll.feature.news.plugin.decorator

import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.Prop
import io.noties.markwon.RenderProps
import io.noties.markwon.core.CoreProps
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImageSize
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.node.Text

/**
 * Allows us to handle iframes and extract images from it, we will only target youtube iframes
 * to return a clickable image which should trigger the youtube
 *
 * <iframe src="https://www.youtube.com/embed/luWcue3t2OU" width="640" height="360" />
 */
internal class FrameTagHandler private constructor() : SimpleTagHandler() {

    private fun getVideoId(src: String) = src.split('/').last()

    private fun getVideoUrl(src: String): String {
        val videoId = getVideoId(src)
        return "https://youtube.com/watch?v=$videoId"
    }

    private fun createImageLink(src: String): String {
        val videoId = getVideoId(src)
        return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    }

    override fun getSpans(
        configuration: MarkwonConfiguration,
        renderProps: RenderProps,
        tag: HtmlTag
    ): Any? {
        val attributes = tag.attributes()
        val source = attributes["src"]
        return if (source?.contains("youtube") == true) {
            val imageSpanFactory = configuration
                .spansFactory()
                .get(Image::class.java)

            val linkSpanFactory = configuration
                .spansFactory()
                .get(Link::class.java)

            val width = attributes["width"]?.toFloat()
            val height = attributes["height"]?.toFloat()
            val imageSize = ImageSize(
                width?.let { ImageSize.Dimension(it, "px") },
                height?.let { ImageSize.Dimension(it, "px") }
            )

            ImageProps.DESTINATION.set(renderProps, createImageLink(source))
            ImageProps.IMAGE_SIZE.set(renderProps, imageSize);
            ImageProps.REPLACEMENT_TEXT_IS_LINK.set(renderProps, false);
            CoreProps.LINK_DESTINATION.set(renderProps, getVideoUrl(source))

            arrayOf(
                imageSpanFactory?.getSpans(configuration, renderProps),
                linkSpanFactory?.getSpans(configuration, renderProps)
            )
        } else {
            // return some sort of unsupported span
            val textSpan = configuration.spansFactory().get(Text::class.java)
            renderProps.set(
                Prop.of("text-literal"),
                "Unsupported embedded element"
            )
            textSpan?.getSpans(configuration, renderProps)
        }
    }

    override fun supportedTags() = listOf("iframe")

    companion object {
        fun create() = FrameTagHandler()
    }
}