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

import co.anitrend.support.crunchyroll.feature.news.extensions.onFrame
import co.anitrend.support.crunchyroll.feature.news.plugin.model.SizeMeasurementUnit
import co.anitrend.support.crunchyroll.feature.news.plugin.model.YouTubeSpanConfiguration
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.html.tag.SimpleTagHandler

/**
 * Allows us to handle iframes and extract images from it, we will only target youtube iframes
 * to return a clickable image which should trigger a youtube handling activity
 *
 * <iframe src="https://www.youtube.com/embed/luWcue3t2OU" width="640" height="360" />
 */
internal class FrameTagHandler private constructor() : SimpleTagHandler() {

    override fun getSpans(
        configuration: MarkwonConfiguration,
        renderProps: RenderProps,
        tag: HtmlTag
    ): Any? {
        val imageSpanConfiguration = YouTubeSpanConfiguration(
            magnificationScale = 1.8f,
            sizeMeasurementUnit = SizeMeasurementUnit.PIXEL,
            configuration = configuration,
            renderProps = renderProps,
            tag = tag
        )

        return imageSpanConfiguration.onFrame()?.toArray()
    }

    override fun supportedTags() = listOf(
        YouTubeSpanConfiguration.IFRAME_TAG
    )

    companion object {
        fun create() = FrameTagHandler()
    }
}