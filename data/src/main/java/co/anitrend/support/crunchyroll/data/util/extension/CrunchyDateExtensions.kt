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

package co.anitrend.support.crunchyroll.data.util.extension

import co.anitrend.arch.extension.util.contract.ISupportDateHelper
import co.anitrend.support.crunchyroll.data.arch.ISO8601Date
import co.anitrend.support.crunchyroll.data.arch.RCF822Date
import co.anitrend.support.crunchyroll.data.util.CrunchyDateHelper.Companion.ISO8601_PATTERN
import co.anitrend.support.crunchyroll.data.util.CrunchyDateHelper.Companion.RCF822_PATTERN
import org.koin.core.context.GlobalContext

val supportDateHelper by lazy {
    GlobalContext.get().koin.get<ISupportDateHelper>()
}

fun ISO8601Date.iso8601ToUnixTime() =
    supportDateHelper.convertToUnixTimeStamp(
        originDate = this,
        inputPattern = ISO8601_PATTERN
    )

fun RCF822Date.rcf822ToUnixTime() =
    supportDateHelper.convertToUnixTimeStamp(
        originDate = this,
        dateTimeFormatter = RCF822_PATTERN
    )