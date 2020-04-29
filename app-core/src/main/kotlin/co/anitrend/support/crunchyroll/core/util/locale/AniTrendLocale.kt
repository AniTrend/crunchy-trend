/*
 *    Copyright 2019 AniTrend
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

package co.anitrend.support.crunchyroll.core.util.locale

import co.anitrend.arch.extension.empty
import java.util.*

/**
 * Represents application locales, we might have the same locale for different countries
 */
enum class AniTrendLocale(val language: String, val country: String? = null) {
    AUTOMATIC(String.empty()),
    ENGLISH_UK(
        language = Locale.UK.language,
        country = Locale.UK.country
    ),
    FRENCH_FRANCE(
        language = Locale.FRANCE.language,
        country = Locale.FRANCE.country
    )
}