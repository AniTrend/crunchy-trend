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

package co.anitrend.support.crunchyroll.data.util

import androidx.annotation.IntRange
import co.anitrend.arch.extension.util.attribute.SeasonType
import co.anitrend.arch.extension.util.date.contract.AbstractSupportDateHelper
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class CrunchyDateUtil : AbstractSupportDateHelper() {

    /**
     * Returns the current month in the form of an [Int].
     *
     * @return [IntRange] between 0 - 11
     */
    val month: Int
        @IntRange(from = 0, to = 11) get() =
            Calendar.getInstance().get(Calendar.MONTH)

    /**
     * Returns the current day in the form of an [Int].
     *
     * @return [IntRange] between 1 - 31
     */
    val day: Int
        @IntRange(from = 0, to = 30) get() =
            Calendar.getInstance().get(Calendar.DATE)

    /**
     * Returns the current year
     */
    val year: Int
        get() = Calendar.getInstance().get(Calendar.YEAR)

    /**
     * @return current seasons name
     */
    override val currentSeason: SeasonType
        get() {
            return when (month) {
                in 2..4 -> SeasonType.SPRING
                in 5..7 -> SeasonType.SUMMER
                in 8..10 -> SeasonType.FALL
                else -> SeasonType.WINTER
            }
        }

    /**
     * Gets the current year + delta, if the season for the year is winter later in the year
     * then the result would be the current year plus the delta
     *
     * @return current year with a given delta
     */
    override fun getCurrentYear(delta: Int): Int {
        return if (month >= 11 && currentSeason == SeasonType.WINTER)
            year + delta
        else year
    }

    companion object {
        internal const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX"
        internal val RCF822_PATTERN = DateTimeFormatter.RFC_1123_DATE_TIME
    }
}