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

package co.anitrend.support.crunchyroll.feature.news.extensions

import android.text.Layout
import android.text.style.AlignmentSpan
import co.anitrend.support.crunchyroll.feature.news.plugin.model.ImageSpanConfiguration
import co.anitrend.support.crunchyroll.feature.news.plugin.model.YouTubeSpanConfiguration
import co.anitrend.support.crunchyroll.feature.news.plugin.model.contract.IPhotoSpan
import io.noties.markwon.Prop
import io.noties.markwon.core.CoreProps
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.node.Text

/**
 * @return list of spans
 */
internal fun ImageSpanConfiguration.onImage(): ArrayList<Any?>? {
    val spans = ArrayList<Any?>(2)
    val attributes = tag.attributes()

    val destination = attributes[IPhotoSpan.SRC_ATTR]
    val styles = attributes[IPhotoSpan.STYLE_ATTR]
    val imageSize = generateImageSize(
        styles, null, attributes[IPhotoSpan.HEIGHT_ATTR]?.toFloat()
    )

    if (destination == null)
        return null

    addPropertiesToImage(destination, imageSize)

    val imageSpanFactory = configuration
        .spansFactory()
        .get(Image::class.java)

    if (imageSize.height?.value != null && imageSize.height.value > cutoffImageSize){
        if (isClickable) {
            val linkSpanFactory = configuration
                .spansFactory()
                .get(Link::class.java)

            CoreProps.LINK_DESTINATION.set(renderProps, destination)
            spans.add(linkSpanFactory?.getSpans(configuration, renderProps))
        }

        spans.add(imageSpanFactory?.getSpans(configuration, renderProps))
    } else spans.add(imageSpanFactory?.getSpans(configuration, renderProps))

    return spans
}

/**
 * @return list of spans
 */
internal fun YouTubeSpanConfiguration.onFrame(): ArrayList<Any?>? {
    val spans = ArrayList<Any?>(2)
    val attributes = tag.attributes()

    val source = attributes[IPhotoSpan.SRC_ATTR]
    val styles = attributes[IPhotoSpan.STYLE_ATTR]
    if (source?.contains(YouTubeSpanConfiguration.LOOKUP_KEY) == true) {
        val imageSpanFactory = configuration
            .spansFactory()
            .get(Image::class.java)

        val linkSpanFactory = configuration
            .spansFactory()
            .get(Link::class.java)

        val imageSize = generateImageSize(
            styles, null, attributes[IPhotoSpan.HEIGHT_ATTR]?.toFloat()
        )

        addPropertiesToImage(source, imageSize)
        spans.add(imageSpanFactory?.getSpans(configuration, renderProps))
        spans.add(linkSpanFactory?.getSpans(configuration, renderProps))
        spans.add(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER))
    } else {
        // return some sort of unsupported span
        val textSpan = configuration
            .spansFactory()
            .get(Text::class.java)

        renderProps.set(
            Prop.of("text-literal"),
            "Unsupported embedded element"
        )
        spans.add(textSpan?.getSpans(configuration, renderProps))
    }

    return spans
}