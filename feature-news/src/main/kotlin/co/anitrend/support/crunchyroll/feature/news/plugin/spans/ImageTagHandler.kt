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

import co.anitrend.support.crunchyroll.feature.news.extensions.onImage
import co.anitrend.support.crunchyroll.feature.news.plugin.model.ImageSpanConfiguration
import co.anitrend.support.crunchyroll.feature.news.plugin.model.SizeMeasurementUnit
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler

internal class ImageTagHandler private constructor() : SimpleTagHandler() {

    override fun getSpans(
        configuration: MarkwonConfiguration,
        renderProps: RenderProps,
        tag: HtmlTag
    ): Any? {
        val imageSpanConfiguration = ImageSpanConfiguration(
            magnificationScale = 1.5f,
            sizeMeasurementUnit = SizeMeasurementUnit.PIXEL,
            configuration = configuration,
            renderProps = renderProps,
            tag = tag,
            cutoffImageSize = 80,
            isClickable = true
        )

        return imageSpanConfiguration.onImage()?.toArray()
    }

    override fun supportedTags() = listOf("img")

    companion object {
        fun create() = ImageTagHandler()
    }
}