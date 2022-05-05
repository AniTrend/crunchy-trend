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

import androidx.annotation.VisibleForTesting
import co.anitrend.support.crunchyroll.feature.news.plugin.model.contract.IPhotoSpan
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.RenderProps
import io.noties.markwon.core.CoreProps
import io.noties.markwon.html.HtmlTag
import io.noties.markwon.image.ImageProps
import io.noties.markwon.image.ImageSize

internal data class YouTubeSpanConfiguration(
    val configuration: MarkwonConfiguration,
    val renderProps: RenderProps,
    val tag: HtmlTag,
    override val magnificationScale: Float,
    override val sizeMeasurementUnit: SizeMeasurementUnit,
    override val isClickable: Boolean = true
) : IPhotoSpan {

    @VisibleForTesting
    fun getVideoId(src: String) = src.split('/').last()

    @VisibleForTesting
    fun getVideoUrl(src: String): String {
        val videoId = getVideoId(src)
        return "https://youtube.com/watch?v=$videoId"
    }

    @VisibleForTesting
    fun createImageLink(src: String): String {
        val videoId = getVideoId(src)
        return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    }

    override fun addPropertiesToImage(source: String, imageSize: ImageSize) {
        ImageProps.DESTINATION.set(renderProps, createImageLink(source))
        ImageProps.IMAGE_SIZE.set(renderProps, imageSize)
        ImageProps.REPLACEMENT_TEXT_IS_LINK.set(renderProps, false)
        CoreProps.LINK_DESTINATION.set(renderProps, getVideoUrl(source))
    }

    companion object {
        internal const val LOOKUP_KEY = "youtube"
        internal const val IFRAME_TAG = "iframe"
    }
}