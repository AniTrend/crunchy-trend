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

package co.anitrend.support.crunchyroll.feature.news.plugin.model.contract

import co.anitrend.support.crunchyroll.feature.news.plugin.model.SizeMeasurementUnit
import io.noties.markwon.image.ImageSize

internal interface IPhotoSpan : ISpanConfiguration {

    val magnificationScale: Float
    val sizeMeasurementUnit: SizeMeasurementUnit

    fun addPropertiesToImage(source: String, imageSize: ImageSize)

    private fun findSizeInStyle(descriptor: String, styles: String?, value: Float?): Float? {
        if (value != null) return value
        val sizeStyle = styles?.split(';')
            ?.firstOrNull { it.contains(descriptor) }
        return runCatching {
            val size = sizeStyle?.run {
                val delimiterPosition = sizeStyle.indexOf(':').plus(1)
                val endPosition = sizeStyle.indexOf(SizeMeasurementUnit.PIXEL.attr).minus(1)
                val newValue = sizeStyle.subSequence(IntRange(delimiterPosition, endPosition))
                newValue.toString().toFloat()
            }
            size
        }.getOrNull()
    }

    fun generateImageSize(styles: String?, width: Float?, height: Float?): ImageSize {
        val autoMeasureHeight = findSizeInStyle(HEIGHT_ATTR, styles, height)
        val autoMeasureWidth = findSizeInStyle(WIDTH_ATTR, styles, width)

        // magnify the images, alternatively we could adjust
        // the magnification based on device dimens
        val heightDimension = autoMeasureHeight?.let {
            ImageSize.Dimension(
                it * magnificationScale,
                sizeMeasurementUnit.attr
            )
        }
        val widthDimension = autoMeasureWidth?.let {
            ImageSize.Dimension(
                it * magnificationScale,
                sizeMeasurementUnit.attr
            )
        }
        return ImageSize(
            widthDimension,
            heightDimension
        )
    }

    companion object {
        internal const val SRC_ATTR = "src"
        internal const val HEIGHT_ATTR = "height"
        internal const val WIDTH_ATTR = "width"
        internal const val STYLE_ATTR = "style"
    }
}