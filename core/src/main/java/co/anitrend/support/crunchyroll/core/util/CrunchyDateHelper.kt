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

package co.anitrend.support.crunchyroll.core.util

import co.anitrend.support.crunchyroll.data.arch.ISO8601Date
import co.anitrend.support.crunchyroll.data.arch.RCF822Date
import io.wax911.support.extension.util.contract.ISupportDateHelper

class CrunchyDateHelper : ISupportDateHelper {

    companion object {
        private const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX"
        private const val RCF822D_PATTERN = "EEE, dd MMM yyyy HH:mm:ss ZZZ"

        fun ISO8601Date.iso8601ToUnixTime(dateHelper: ISupportDateHelper) =
            dateHelper.convertToUnixTimeStamp(originDate = this, inputPattern = ISO8601_PATTERN)

        fun RCF822Date.rcf822ToUnixTime(dateHelper: ISupportDateHelper) =
            dateHelper.convertToUnixTimeStamp(this, inputPattern = RCF822D_PATTERN)
    }
}