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

package co.anitrend.support.crunchyroll.feature.series.extensions

import androidx.appcompat.widget.AppCompatImageView
import co.anitrend.support.crunchyroll.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.core.common.DEFAULT_ANIMATION_DURATION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun AppCompatImageView.setImageFromUrl(url: String?) {
    if (context is CoroutineScope) {
        val scope = context as CoroutineScope
        scope.launch {
            delay(DEFAULT_ANIMATION_DURATION)
            setImageUrl(url)
        }
    }
    else
        setImageUrl(url)
}