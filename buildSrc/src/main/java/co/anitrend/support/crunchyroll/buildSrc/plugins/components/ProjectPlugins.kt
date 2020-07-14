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

package co.anitrend.support.crunchyroll.buildSrc.plugins.components

import co.anitrend.support.crunchyroll.buildSrc.common.app
import co.anitrend.support.crunchyroll.buildSrc.common.domain
import co.anitrend.support.crunchyroll.buildSrc.common.navigation
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer

private fun addAndroidPlugin(name: String, pluginContainer: PluginContainer) {
    if (name == app) pluginContainer.apply("com.android.application")
    else pluginContainer.apply("com.android.library")
}

private fun addAnnotationProcessor(name: String, pluginContainer: PluginContainer) {
    if (name != domain || name != navigation)
        pluginContainer.apply("kotlin-kapt")
}

internal fun Project.configurePlugins() {
    addAndroidPlugin(project.name, plugins)
    plugins.apply("kotlin-android")
    plugins.apply("kotlin-android-extensions")
    addAnnotationProcessor(project.name, plugins)
}