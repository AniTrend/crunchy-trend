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

package co.anitrend.support.crunchyroll.buildSrc.common

object Versions {

    private const val major = 0
    private const val minor = 8
    private const val patch = 0
    private const val candidate = 0

    private const val channel = "alpha"

    const val compileSdk = 30
    const val targetSdk = 30
    const val minSdk = 21

    /**
     * **RR**_X.Y.Z_
     * > **RR** reserved for build flavours and **X.Y.Z** follow the [versionName] convention
     */
    const val versionCode = major.times(10_000_000) +
            minor.times(100_000) +
            patch.times(100) +
            candidate.times(1)

    /**
     * Naming schema: X.Y.Z-variant##
     * > **X**(Major).**Y**(Minor).**Z**(Patch)
     */
    val versionName = if (candidate > 0)
        "$major.$minor.$patch-$channel$candidate"
    else
        "$major.$minor.$patch"

    const val junit = "4.13.2"

    const val timber = "5.0.1"
    const val threeTenBp = "1.3.1"

    const val debugDB = "1.0.6"
    const val treesSence = "1.0.4"

    const val liquidSwipe = "1.3"

    const val prettyTime = "4.0.4.Final"
    const val scalingImageView = "3.10.0"
    const val serializationConverter = "0.8.0"

    const val jsoup = "1.13.1"

    const val ktlint = "0.40.0"

    const val deeplink = "0.3.1"
}