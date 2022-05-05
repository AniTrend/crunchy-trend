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

package co.anitrend.support.crunchyroll.data.api.helper

import kotlin.random.Random

internal object SessionHelper {
    private val whiteList = arrayOf<Short>(
        0x61, 0x62, 0x63, 0x64, 0x65, 0x66,
        0x30, 0x31, 0x32, 0x33, 0x34, 0x35,
        0x36, 0x37, 0x38, 0x39
    )

    fun createDummyDeviceId(start: Int = 0, length: Int = 12): String {
        val end = whiteList.size - 1
        val builder = StringBuilder(length)
        repeat(length) {
            val random = Random.nextInt(start, end)
            builder.append(whiteList[random].toInt().toChar())
        }
        return builder.toString()
    }
}