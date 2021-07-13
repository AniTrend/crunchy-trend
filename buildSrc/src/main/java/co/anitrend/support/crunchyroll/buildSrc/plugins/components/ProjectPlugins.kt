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
import co.anitrend.support.crunchyroll.buildSrc.common.isAppModule
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.baseExtension
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer


private fun addAndroidPlugin(project: Project, pluginContainer: PluginContainer) {
    if (project.isAppModule()) pluginContainer.apply("com.android.application")
    else pluginContainer.apply("com.android.library")
}

private fun addKotlinAndroidPlugin(pluginContainer: PluginContainer) {
    pluginContainer.apply("kotlin-android")
    pluginContainer.apply("com.diffplug.spotless")
}

private fun addAnnotationProcessor(project: Project, pluginContainer: PluginContainer) {
    if (project.name != domain || project.name != app || project.name != navigation)
        pluginContainer.apply("kotlin-kapt")
}

private fun addKotlinAndroidExtensions(project: Project, pluginContainer: PluginContainer) {
    if (project.name != domain)
        pluginContainer.apply("kotlin-parcelize")
}

internal fun Project.configurePlugins() {
    addAndroidPlugin(project, plugins)
    addKotlinAndroidPlugin(plugins)
    addKotlinAndroidExtensions(project, plugins)
    addAnnotationProcessor(project, plugins)
}

internal fun Project.configureAdditionalPlugins() {
    /*if (isAppModule()) {
        println("Applying additional google plugins")
        if (file("google-services.json").exists()) {
            plugins.apply("com.google.gms.google-services")
            plugins.apply("com.google.firebase.crashlytics")
        } else println("google-services.json cannot be found and will not be using any of the google plugins")
    }*/
    if (isAppModule()) {
        baseExtension().variantFilter {
            println("VariantFilter { defaultConfig: ${defaultConfig.name}, buildType: ${buildType.name}, flavors: [${flavors.joinToString { it.name }}], name: $name }")
            if (flavors.isNotEmpty() && flavors.first().name == "google") {
                println("Applying additional google plugins on -> variant: $name | type: ${buildType.name}")
                if (file("google-services.json").exists()) {
                    plugins.apply("com.google.gms.google-services")
                    plugins.apply("com.google.firebase.crashlytics")
                } else println("google-services.json cannot be found and will not be using any of the google plugins")
            }
        }
    }
}