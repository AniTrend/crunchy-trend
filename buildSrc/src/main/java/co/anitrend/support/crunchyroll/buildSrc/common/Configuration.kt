/*
 *    Copyright 2022 AniTrend
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

package co.anitrend.support.crunchyroll.buildSrc.common


object Configuration {

    private const val major = 0
    private const val minor = 8
    private const val patch = 0
    private const val candidate = 0

    private const val channel = "alpha"

    const val compileSdk = 31
    const val targetSdk = 31
    const val minSdk = 21

    /**
     * **RR**_X.Y.Z_
     * > **RR** reserved for build flavours and **X.Y.Z** follow the [versionName] convention
     */
    const val versionCode = major.times(1_000_000_000) +
            minor.times(1_000_000) +
            patch.times(1_000) +
            candidate

    /**
     * Naming schema: X.Y.Z-variant##
     * > **X**(Major).**Y**(Minor).**Z**(Patch)
     */
    val versionName = if (candidate > 0)
        "$major.$minor.$patch-$channel$candidate"
    else
        "$major.$minor.$patch"
}