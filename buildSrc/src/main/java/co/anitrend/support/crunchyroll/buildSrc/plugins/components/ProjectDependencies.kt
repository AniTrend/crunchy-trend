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

import co.anitrend.support.crunchyroll.buildSrc.Libraries
import co.anitrend.support.crunchyroll.buildSrc.common.*
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.implementation
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.runtime
import co.anitrend.support.crunchyroll.buildSrc.plugins.strategy.DependencyStrategy
import org.gradle.api.Project
import org.gradle.kotlin.dsl.exclude


private fun Project.applyFeatureModuleDependencies() {
    println("Applying shared dependencies for feature module -> $path")

    dependencies.implementation(Libraries.AniTrend.Arch.ui)
    dependencies.implementation(Libraries.AniTrend.Arch.ext)
    dependencies.implementation(Libraries.AniTrend.Arch.core)
    dependencies.implementation(Libraries.AniTrend.Arch.data)
    dependencies.implementation(Libraries.AniTrend.Arch.theme)
    dependencies.implementation(Libraries.AniTrend.Arch.domain)
    dependencies.implementation(Libraries.AniTrend.Arch.recycler)

    dependencies.implementation(Libraries.AndroidX.Core.coreKtx)
    dependencies.implementation(Libraries.AndroidX.Work.runtimeKtx)
    dependencies.implementation(Libraries.AndroidX.Paging.runtimeKtx)
    dependencies.implementation(Libraries.AndroidX.Activity.activityKtx)
    dependencies.implementation(Libraries.AndroidX.Fragment.fragmentKtx)
    dependencies.implementation(Libraries.AndroidX.StartUp.startUpRuntime)
    dependencies.implementation(Libraries.AndroidX.Collection.collectionKtx)
    dependencies.implementation(Libraries.AndroidX.SwipeRefresh.swipeRefreshLayout)
    dependencies.implementation(Libraries.AndroidX.ConstraintLayout.constraintLayout)

    dependencies.implementation(Libraries.Google.Material.material)

    dependencies.implementation(Libraries.Coil.coil)

    dependencies.implementation(project(":$core"))
    dependencies.implementation(project(":$data"))
    dependencies.implementation(project(":$domain"))
    dependencies.implementation(project(":$navigation"))
    dependencies.implementation(project(":$androidCore"))
}

private fun Project.applyAppModuleDependencies() {
    featureModules.forEach { module ->
        println("Adding runtimeOnly dependency :$module -> ${project.path}")
        dependencies.runtime(project(":$module"))
    }

    baseModules.forEach { module ->
        if (module != app) {
            println("Adding base module dependency :$module -> ${project.path}")
            dependencies.implementation(project(":$module"))
        }
    }

    androidModules.forEach { module ->
        println("Adding android core module dependency :$module -> ${project.path}")
        dependencies.implementation(project(":$module"))
    }

    dependencies.implementation(Libraries.Google.Material.material)

    dependencies.implementation(Libraries.AndroidX.Core.coreKtx)
    dependencies.implementation(Libraries.AndroidX.Work.runtimeKtx)
    dependencies.implementation(Libraries.AndroidX.Activity.activityKtx)
    dependencies.implementation(Libraries.AndroidX.Fragment.fragmentKtx)
    dependencies.implementation(Libraries.AndroidX.StartUp.startUpRuntime)
    dependencies.implementation(Libraries.AndroidX.ConstraintLayout.constraintLayout)

    dependencies.implementation(Libraries.Coil.coil)
}

private fun Project.applyBaseModuleDependencies() {
    println("Applying base module dependencies for module -> $path")
    when (name) {
        core -> {
            dependencies.implementation(project(":$androidCore"))
            dependencies.implementation(project(":$navigation"))
            dependencies.implementation(project(":$domain"))
            dependencies.implementation(project(":$data"))

            dependencies.implementation(Libraries.Google.Material.material)

            dependencies.implementation(Libraries.AndroidX.Core.coreKtx)
            dependencies.implementation(Libraries.AndroidX.Work.runtimeKtx)
            dependencies.implementation(Libraries.AndroidX.Activity.activityKtx)
            dependencies.implementation(Libraries.AndroidX.Fragment.fragmentKtx)
            dependencies.implementation(Libraries.AndroidX.StartUp.startUpRuntime)
            dependencies.implementation(Libraries.AndroidX.SwipeRefresh.swipeRefreshLayout)
            dependencies.implementation(Libraries.AndroidX.ConstraintLayout.constraintLayout)

            dependencies.implementation(Libraries.AndroidX.Collection.collectionKtx)
            dependencies.implementation(Libraries.AndroidX.Recycler.recyclerView)
            dependencies.implementation(Libraries.AndroidX.Paging.runtimeKtx)

            dependencies.implementation(Libraries.Coil.coil)
            dependencies.implementation(Libraries.Coil.gif)
            dependencies.implementation(Libraries.Coil.svg)
            dependencies.implementation(Libraries.Coil.video)
        }
        data -> {
            dependencies.implementation(project(":$domain"))

            dependencies.implementation(Libraries.AndroidX.Core.coreKtx)
            dependencies.implementation(Libraries.AndroidX.Paging.common)
            dependencies.implementation(Libraries.AndroidX.Paging.runtime)
            dependencies.implementation(Libraries.AndroidX.Paging.runtimeKtx)
            dependencies.implementation(Libraries.AndroidX.Room.runtime)
            dependencies.implementation(Libraries.AndroidX.Room.ktx)
            dependencies.add("kapt", Libraries.AndroidX.Room.compiler)

            dependencies.implementation(Libraries.Square.Retrofit.retrofit)
            dependencies.implementation(Libraries.Square.Retrofit.gsonConverter)
            dependencies.implementation(Libraries.Square.Retrofit.xmlConverter) {
                exclude(group = "xpp3", module = "xpp3")
                exclude(group = "stax", module = "stax-api")
                exclude(group = "stax", module = "stax")
            }
            dependencies.implementation(Libraries.Square.OkHttp.logging)
        }
        domain -> {
            dependencies.implementation(Libraries.AniTrend.Arch.domain)
        }
        navigation -> {
            dependencies.implementation(Libraries.AndroidX.Core.coreKtx)
            dependencies.implementation(Libraries.AndroidX.Activity.activityKtx)
            dependencies.implementation(Libraries.AndroidX.Collection.collectionKtx)
            dependencies.implementation(Libraries.AndroidX.Fragment.fragment)
        }
    }
}

private fun Project.applyAndroidCoreModuleDependencies() {
    println("Applying core feature dependencies for feature module -> $path")

    dependencies.implementation(Libraries.AniTrend.Arch.ui)
    dependencies.implementation(Libraries.AniTrend.Arch.ext)
    dependencies.implementation(Libraries.AniTrend.Arch.core)
    dependencies.implementation(Libraries.AniTrend.Arch.data)
    dependencies.implementation(Libraries.AniTrend.Arch.theme)
    dependencies.implementation(Libraries.AniTrend.Arch.domain)

    dependencies.implementation(Libraries.AndroidX.Core.coreKtx)
    dependencies.implementation(Libraries.AndroidX.Work.runtimeKtx)
    dependencies.implementation(Libraries.AndroidX.Paging.runtimeKtx)
    dependencies.implementation(Libraries.AndroidX.Activity.activityKtx)
    dependencies.implementation(Libraries.AndroidX.Fragment.fragmentKtx)
    dependencies.implementation(Libraries.AndroidX.StartUp.startUpRuntime)
    dependencies.implementation(Libraries.AndroidX.Collection.collectionKtx)
    dependencies.implementation(Libraries.AndroidX.ConstraintLayout.constraintLayout)

    dependencies.implementation(Libraries.Google.Material.material)

    dependencies.implementation(Libraries.Coil.coil)

    dependencies.implementation(project(":$data"))
    dependencies.implementation(project(":$domain"))
    dependencies.implementation(project(":$navigation"))
    if (!isAndroidCoreModule()) {
        dependencies.implementation(project(":$androidCore"))
    }
}

private fun Project.applyCommonModuleDependencies() {
    println("Applying common feature dependencies for feature module -> $path")

    dependencies.implementation(Libraries.AniTrend.Arch.ui)
    dependencies.implementation(Libraries.AniTrend.Arch.ext)
    dependencies.implementation(Libraries.AniTrend.Arch.core)
    dependencies.implementation(Libraries.AniTrend.Arch.data)
    dependencies.implementation(Libraries.AniTrend.Arch.theme)
    dependencies.implementation(Libraries.AniTrend.Arch.domain)
    dependencies.implementation(Libraries.AniTrend.Arch.recycler)

    dependencies.implementation(Libraries.AndroidX.Core.coreKtx)
    dependencies.implementation(Libraries.AndroidX.Work.runtimeKtx)
    dependencies.implementation(Libraries.AndroidX.Paging.runtimeKtx)
    dependencies.implementation(Libraries.AndroidX.Activity.activityKtx)
    dependencies.implementation(Libraries.AndroidX.Fragment.fragmentKtx)
    dependencies.implementation(Libraries.AndroidX.StartUp.startUpRuntime)
    dependencies.implementation(Libraries.AndroidX.Collection.collectionKtx)
    dependencies.implementation(Libraries.AndroidX.ConstraintLayout.constraintLayout)

    dependencies.implementation(Libraries.Google.Material.material)

    dependencies.implementation(Libraries.Coil.coil)

    dependencies.implementation(project(":$androidCore"))
    dependencies.implementation(project(":$core"))
    dependencies.implementation(project(":$data"))
    dependencies.implementation(project(":$domain"))
    dependencies.implementation(project(":$navigation"))
}

internal fun Project.configureDependencies() {
    val dependencyStrategy = DependencyStrategy(project.name)
    dependencies.implementation(
        fileTree("libs") {
            include("*.jar")
        }
    )
    dependencyStrategy.applyDependenciesOn(dependencies)

    if (isFeatureModule()) applyFeatureModuleDependencies()
    if (isAppModule()) applyAppModuleDependencies()
    if (isBaseModule()) applyBaseModuleDependencies()
    if (isCoreFeatureModule()) applyAndroidCoreModuleDependencies()
    if (isCommonFeatureModule()) applyCommonModuleDependencies()
}