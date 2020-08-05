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
import co.anitrend.support.crunchyroll.buildSrc.common.core
import co.anitrend.support.crunchyroll.buildSrc.common.data
import co.anitrend.support.crunchyroll.buildSrc.common.domain
import co.anitrend.support.crunchyroll.buildSrc.common.navigation
import co.anitrend.support.crunchyroll.buildSrc.plugins.strategy.DependencyStrategy
import org.gradle.api.Project
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.exclude


private fun Project.applyFeatureModuleDependencies() {
    println("Applying shared dependencies for feature module -> $path")

    dependencies.add("implementation", Libraries.AniTrend.Arch.ui)
    dependencies.add("implementation", Libraries.AniTrend.Arch.ext)
    dependencies.add("implementation", Libraries.AniTrend.Arch.core)
    dependencies.add("implementation", Libraries.AniTrend.Arch.data)
    dependencies.add("implementation", Libraries.AniTrend.Arch.theme)
    dependencies.add("implementation", Libraries.AniTrend.Arch.domain)
    dependencies.add("implementation", Libraries.AniTrend.Arch.recycler)

    dependencies.add("implementation", Libraries.AndroidX.Core.coreKtx)
    dependencies.add("implementation", Libraries.AndroidX.Work.runtimeKtx)
    dependencies.add("implementation", Libraries.AndroidX.Paging.runtimeKtx)
    dependencies.add("implementation", Libraries.AndroidX.Activity.activityKtx)
    dependencies.add("implementation", Libraries.AndroidX.Fragment.fragmentKtx)
    dependencies.add("implementation", Libraries.AndroidX.StartUp.startUpRuntime)
    dependencies.add("implementation", Libraries.AndroidX.Collection.collectionKtx)
    dependencies.add("implementation", Libraries.AndroidX.SwipeRefresh.swipeRefreshLayout)
    dependencies.add("implementation", Libraries.AndroidX.ContraintLayout.constraintLayout)

    dependencies.add("implementation", Libraries.Google.Material.material)

    dependencies.add("implementation", Libraries.Glide.glide)
    dependencies.add("implementation", Libraries.Coil.coil)

    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.conductor)
    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.lifecycle)
    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.transitions)
    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.viewPage)

    dependencies.add("implementation", project(":$core"))
    dependencies.add("implementation", project(":$data"))
    dependencies.add("implementation", project(":$domain"))
    dependencies.add("implementation", project(":$navigation"))
    dependencies.add("implementation", project(":$androidCore"))
}

private fun Project.applyAppModuleDependencies() {
    featureModules.forEach { module ->
        println("Adding runtimeOnly dependency :$module -> ${project.path}")
        dependencies.add("runtimeOnly", project(":$module"))
    }

    baseModules.forEach { module ->
        if (module != app) {
            println("Adding base module dependency :$module -> ${project.path}")
            dependencies.add("implementation", project(":$module"))
        }
    }

    androidModules.forEach { module ->
        println("Adding android core module dependency :$module -> ${project.path}")
        dependencies.add("implementation", project(":$module"))
    }

    dependencies.add("implementation", Libraries.Google.Material.material)

    dependencies.add("implementation", Libraries.AndroidX.Core.coreKtx)
    dependencies.add("implementation", Libraries.AndroidX.Work.runtimeKtx)
    dependencies.add("implementation", Libraries.AndroidX.Activity.activityKtx)
    dependencies.add("implementation", Libraries.AndroidX.Fragment.fragmentKtx)
    dependencies.add("implementation", Libraries.AndroidX.StartUp.startUpRuntime)
    dependencies.add("implementation", Libraries.AndroidX.ContraintLayout.constraintLayout)

    dependencies.add("implementation", Libraries.Coil.coil)

    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.conductor)
    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.lifecycle)
    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.transitions)
    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.viewPage)
}

private fun Project.applyBaseModuleDependencies() {
    println("Applying base module dependencies for module -> $path")
    when (name) {
        core -> {
            dependencies.add("implementation", project(":$navigation"))
            dependencies.add("implementation", project(":$domain"))
            dependencies.add("implementation", project(":$data"))

            dependencies.add("implementation", Libraries.Google.Material.material)

            dependencies.add("implementation", Libraries.AndroidX.Core.coreKtx)
            dependencies.add("implementation", Libraries.AndroidX.Work.runtimeKtx)
            dependencies.add("implementation", Libraries.AndroidX.Activity.activityKtx)
            dependencies.add("implementation", Libraries.AndroidX.Fragment.fragmentKtx)
            dependencies.add("implementation", Libraries.AndroidX.StartUp.startUpRuntime)
            dependencies.add("implementation", Libraries.AndroidX.SwipeRefresh.swipeRefreshLayout)
            dependencies.add("implementation", Libraries.AndroidX.ContraintLayout.constraintLayout)

            dependencies.add("implementation", Libraries.AndroidX.Collection.collectionKtx)
            dependencies.add("implementation", Libraries.AndroidX.Recycler.recyclerView)
            dependencies.add("implementation", Libraries.AndroidX.Paging.runtimeKtx)

            dependencies.add("implementation", Libraries.Coil.coil)
            dependencies.add("implementation", Libraries.Coil.gif)
            dependencies.add("implementation", Libraries.Coil.svg)
            dependencies.add("implementation", Libraries.Coil.video)
            dependencies.add("implementation", Libraries.Glide.glide)
            dependencies.add("kapt", Libraries.Glide.compiler)

            dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.conductor)
            dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.lifecycle)
        }
        data -> {
            dependencies.add("implementation", project(":$domain"))

            dependencies.add("implementation", Libraries.AndroidX.Core.coreKtx)
            dependencies.add("implementation", Libraries.AndroidX.Paging.common)
            dependencies.add("implementation", Libraries.AndroidX.Paging.runtime)
            dependencies.add("implementation", Libraries.AndroidX.Paging.runtimeKtx)
            dependencies.add("implementation", Libraries.AndroidX.Room.runtime)
            dependencies.add("implementation", Libraries.AndroidX.Room.ktx)
            dependencies.add("kapt", Libraries.AndroidX.Room.compiler)

            dependencies.add("implementation", Libraries.Square.Retrofit.retrofit)
            dependencies.add("implementation", Libraries.Square.Retrofit.gsonConverter)
            dependencies.add("implementation", Libraries.Square.Retrofit.xmlConverter) {
                exclude(group = "xpp3", module = "xpp3")
                exclude(group = "stax", module = "stax-api")
                exclude(group = "stax", module = "stax")
            }
            dependencies.add("implementation", Libraries.Square.OkHttp.logging)
        }
        domain -> {
            dependencies.add("implementation", Libraries.AniTrend.Arch.domain)
        }
        navigation -> {
            dependencies.add("implementation", Libraries.AndroidX.Core.coreKtx)
            dependencies.add("implementation", Libraries.AndroidX.Activity.activityKtx)
            dependencies.add("implementation", Libraries.AndroidX.Collection.collectionKtx)
            dependencies.add("implementation", Libraries.AndroidX.Fragment.fragment)
        }
    }
}

private fun Project.applyAndroidCoreModuleDependencies() {
    println("Applying core feature dependencies for feature module -> $path")

    dependencies.add("implementation", Libraries.AniTrend.Arch.ui)
    dependencies.add("implementation", Libraries.AniTrend.Arch.ext)
    dependencies.add("implementation", Libraries.AniTrend.Arch.core)
    dependencies.add("implementation", Libraries.AniTrend.Arch.data)
    dependencies.add("implementation", Libraries.AniTrend.Arch.theme)
    dependencies.add("implementation", Libraries.AniTrend.Arch.domain)

    dependencies.add("implementation", Libraries.AndroidX.Core.coreKtx)
    dependencies.add("implementation", Libraries.AndroidX.Work.runtimeKtx)
    dependencies.add("implementation", Libraries.AndroidX.Paging.runtimeKtx)
    dependencies.add("implementation", Libraries.AndroidX.Activity.activityKtx)
    dependencies.add("implementation", Libraries.AndroidX.Fragment.fragmentKtx)
    dependencies.add("implementation", Libraries.AndroidX.StartUp.startUpRuntime)
    dependencies.add("implementation", Libraries.AndroidX.Collection.collectionKtx)
    dependencies.add("implementation", Libraries.AndroidX.ContraintLayout.constraintLayout)

    dependencies.add("implementation", Libraries.Google.Material.material)

    dependencies.add("implementation", Libraries.Coil.coil)

    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.conductor)
    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.lifecycle)
    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.transitions)
    dependencies.add("implementation", Libraries.BlueLineLabs.Conductor.viewPage)

    dependencies.add("implementation", project(":$core"))
    dependencies.add("implementation", project(":$data"))
    dependencies.add("implementation", project(":$domain"))
    dependencies.add("implementation", project(":$navigation"))
    if (!isAndroidCoreModule()) {
        dependencies.add("implementation", project(":$androidCore"))
    }
}

private fun Project.applyCommonModuleDependencies() {
    println("Applying common feature dependencies for feature module -> $path")

    dependencies.add("implementation", Libraries.AniTrend.Arch.ui)
    dependencies.add("implementation", Libraries.AniTrend.Arch.ext)
    dependencies.add("implementation", Libraries.AniTrend.Arch.core)
    dependencies.add("implementation", Libraries.AniTrend.Arch.data)
    dependencies.add("implementation", Libraries.AniTrend.Arch.theme)
    dependencies.add("implementation", Libraries.AniTrend.Arch.domain)
    dependencies.add("implementation", Libraries.AniTrend.Arch.recycler)

    dependencies.add("implementation", Libraries.AndroidX.Core.coreKtx)
    dependencies.add("implementation", Libraries.AndroidX.Work.runtimeKtx)
    dependencies.add("implementation", Libraries.AndroidX.Paging.runtimeKtx)
    dependencies.add("implementation", Libraries.AndroidX.Activity.activityKtx)
    dependencies.add("implementation", Libraries.AndroidX.Fragment.fragmentKtx)
    dependencies.add("implementation", Libraries.AndroidX.StartUp.startUpRuntime)
    dependencies.add("implementation", Libraries.AndroidX.Collection.collectionKtx)
    dependencies.add("implementation", Libraries.AndroidX.ContraintLayout.constraintLayout)

    dependencies.add("implementation", Libraries.Google.Material.material)

    dependencies.add("implementation", Libraries.Coil.coil)
    dependencies.add("implementation", Libraries.Glide.glide)

    dependencies.add("implementation", project(":$androidCore"))
    dependencies.add("implementation", project(":$core"))
    dependencies.add("implementation", project(":$data"))
    dependencies.add("implementation", project(":$domain"))
    dependencies.add("implementation", project(":$navigation"))
}

internal fun Project.configureDependencies() {
    val dependencyStrategy = DependencyStrategy(project.name)
    dependencies.add("implementation",
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