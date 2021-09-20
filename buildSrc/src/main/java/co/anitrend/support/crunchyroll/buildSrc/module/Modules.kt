/*
 *    Copyright 2021 AniTrend
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

package co.anitrend.support.crunchyroll.buildSrc.module

internal object Modules {

    const val appModulePattern = "app-"
    const val androidModulePattern = "android-"
    const val featureModulePattern = "feature-"
    const val commonModulePattern = "ui-"

    interface Module {
        val id: String

        /**
         * @return Formatted id of module as a path string
         */
        fun path(): String = ":$id"
    }

    enum class App(override val id: String) : Module {
        Main("app"),
        Core("app-core"),
        Data("app-data"),
        Domain("app-domain"),
        Navigation("app-navigation")
    }

    enum class Android(override val id: String) : Module {
        Core("android-core")
    }

    enum class Common(override val id: String) : Module {
        ImageViewer("ui-image-viewer"),
        Series("ui-shared-series")
    }

    enum class Feature(override val id: String) : Module {
        Auth("feature-auth"),
        Catalog("feature-catalog"),
        Collection("feature-collection"),
        Discover("feature-discover"),
        Listing("feature-listing"),
        Media("feature-media"),
        News("feature-news"),
        Player("feature-player"),
        Series("feature-series"),
        Settings("feature-settings"),
        Search("feature-search"),
        Splash("feature-splash"),
    }
}
