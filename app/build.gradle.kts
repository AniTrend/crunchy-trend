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

android {
    buildFeatures {
        viewBinding = true
    }
    namespace = "co.anitrend.support.crunchyroll"
}

dependencies {

    /** Material Design */
    implementation(Libraries.Google.Material.material)
    implementation(Libraries.Google.Firebase.Analytics.analyticsKtx)
    implementation(Libraries.Google.Firebase.Crashlytics.crashlytics)

    // debugImplementation because LeakCanary should only run in debug builds.
    debugImplementation(Libraries.Square.LeakCanary.leakCanary)
    debugImplementation(Libraries.debugDb)
}

if (file("google-services.json").exists()) {
    plugins.apply("com.google.gms.google-services")
    plugins.apply("com.google.firebase.crashlytics")
}
