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
    private const val minor = 6
    private const val patch = 2
    private const val candidate = 10

    const val compileSdk = 30
    const val targetSdk = 30
    const val minSdk = 21

    const val versionCode = major.times(10_000) +
            minor.times(1000) +
            patch.times(100) +
            candidate.times(10)

    const val versionName = "$major.$minor.$patch-alpha$candidate"

    const val mockk = "1.10.2"
    const val junit = "4.13.1"

    const val timber = "4.7.1"
    const val threeTenBp = "1.3.0"

    const val debugDB = "1.0.6"
    const val treesSence = "0.3.2"

    const val scalingImageView = "3.10.0"
    const val serializationConverter = "0.8.0"
}