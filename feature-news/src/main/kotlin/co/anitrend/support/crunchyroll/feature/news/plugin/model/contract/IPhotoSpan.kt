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

    fun generateImageSize(width: Float?, height: Float?): ImageSize {
        // magnify the images, alternatively we could adjust
        // the magnification based on device dimens
        val heightDimension = height?.let {
            ImageSize.Dimension(
                it * magnificationScale,
                sizeMeasurementUnit.attr
            )
        }
        val widthDimension = width?.let {
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
    }
}