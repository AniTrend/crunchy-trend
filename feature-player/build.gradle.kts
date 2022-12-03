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

import co.anitrend.support.crunchyroll.buildSrc.Libraries

plugins {
    id("co.anitrend.crunchyroll.plugin")
}

//val exoMedia = "dev~5.0-SNAPSHOT"
val exoMedia = "6.0.0"
// need to fix multiple publications with same id
val playlist = "2.1.1"

dependencies {
    implementation(Libraries.Square.OkHttp.okhttp)

    implementation(Libraries.AndroidX.Media2.media2)

    /** Exo media */
    implementation("com.github.wax911:ExoMedia:$exoMedia")

    implementation("com.github.wax911:PlaylistCore:$playlist")

    /** Exo player extensions */
    implementation(Libraries.Google.Exo.workManager)
    implementation(Libraries.Google.Exo.okHttp)

    /** Adding exo player hls since we use media formatter in exo-media */
    implementation(Libraries.Google.Exo.hls)

    /** Adding ui module */
    implementation(Libraries.Google.Exo.ui)

    implementation(Libraries.MaterialDialogs.core)
}
android {
    namespace = "co.anitrend.support.crunchyroll.feature.player"
}
