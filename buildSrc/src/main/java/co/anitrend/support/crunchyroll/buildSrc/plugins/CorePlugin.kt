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

package co.anitrend.support.crunchyroll.buildSrc.plugins

import co.anitrend.support.crunchyroll.buildSrc.plugins.components.configureAndroid
import co.anitrend.support.crunchyroll.buildSrc.plugins.components.configureDependencies
import co.anitrend.support.crunchyroll.buildSrc.plugins.components.configureOptions
import co.anitrend.support.crunchyroll.buildSrc.plugins.components.configurePlugins
import org.gradle.api.Plugin
import org.gradle.api.Project


open class CorePlugin : Plugin<Project> {

    /**
     * Inspecting available extensions
     */
    @Suppress("UnstableApiUsage")
    internal fun Project.availableExtensions() {
        val extensionSchema = project.extensions.extensionsSchema
        extensionSchema.forEach {
            println("Available extension for module ${project.path}: ${it.name} -> ${it.publicType}")
        }
    }

    /**
     * Inspecting available components
     */
    @Suppress("UnstableApiUsage")
    internal fun Project.availableComponents() {
        val collectionSchema = project.components.asMap
        collectionSchema.forEach {
            println("Available component for module ${project.path}: ${it.key} -> ${it.value}")
        }
    }

    /**
     * Apply this plugin to the given target object.
     *
     * @param target The target object
     */
    override fun apply(target: Project) {
        target.configurePlugins()
        target.availableExtensions()
        target.availableComponents()
        target.configureAndroid()
        target.configureOptions()
        target.configureDependencies()
    }
}