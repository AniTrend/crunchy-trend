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

val jsoup = "1.13.1"
val betterLinkMovement = "2.2.0"

dependencies {
    implementation("org.jsoup:jsoup:$jsoup")

    /** Markwon */
    implementation(Libraries.Markwon.core)
    implementation(Libraries.Markwon.html)
    implementation(Libraries.Markwon.linkify)
    implementation(Libraries.Markwon.glide)

    implementation(Libraries.Blitz.blitz)

    androidTestImplementation(Libraries.CashApp.Copper.copper)

    implementation("me.saket:better-link-movement-method:$betterLinkMovement")
}
