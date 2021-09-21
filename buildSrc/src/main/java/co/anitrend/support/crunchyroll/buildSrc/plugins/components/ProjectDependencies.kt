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
import co.anitrend.support.crunchyroll.buildSrc.module.*
import co.anitrend.support.crunchyroll.buildSrc.extensions.*
import co.anitrend.support.crunchyroll.buildSrc.extensions.implementation
import co.anitrend.support.crunchyroll.buildSrc.extensions.runtime
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

    dependencies.implementation(project(Modules.App.Core.path()))
    dependencies.implementation(project(Modules.App.Data.path()))
    dependencies.implementation(project(Modules.App.Domain.path()))
    dependencies.implementation(project(Modules.App.Navigation.path()))
    dependencies.implementation(project(Modules.Android.Core.path()))
}

private fun Project.applyAppModuleDependencies() {
    Modules.Feature.values().forEach { module ->
        println("Adding runtimeOnly dependency :$module -> ${project.path}")
        dependencies.runtime(project(":${module.path()}"))
    }

    Modules.App.values().forEach { module ->
        if (module != Modules.App.Main) {
            println("Adding base module dependency :${module.path()} -> ${project.path}")
            dependencies.implementation(project(":${module.path()}"))
        }
    }

    Modules.Android.values().forEach { module ->
        println("Adding android core module dependency :${module.path()} -> ${project.path}")
        dependencies.implementation(project(":${module.path()}"))
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
        Modules.App.Core.id -> {
            dependencies.implementation(project(Modules.Android.Core.path()))
            dependencies.implementation(project(Modules.App.Navigation.path()))
            dependencies.implementation(project(Modules.App.Domain.path()))
            dependencies.implementation(project(Modules.App.Data.path()))

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
        Modules.App.Data.id -> {
            dependencies.implementation(project(Modules.App.Domain.path()))

            dependencies.implementation(Libraries.AndroidX.Core.coreKtx)
            dependencies.implementation(Libraries.AndroidX.Paging.common)
            dependencies.implementation(Libraries.AndroidX.Paging.runtime)
            dependencies.implementation(Libraries.AndroidX.Paging.runtimeKtx)
            dependencies.implementation(Libraries.AndroidX.Room.runtime)
            dependencies.implementation(Libraries.AndroidX.Room.ktx)
            dependencies.kapt(Libraries.AndroidX.Room.compiler)

            dependencies.implementation(Libraries.Square.Retrofit.retrofit)
            dependencies.implementation(Libraries.Square.Retrofit.gsonConverter)
            dependencies.implementation(Libraries.Square.Retrofit.xmlConverter) {
                exclude(group = "xpp3", module = "xpp3")
                exclude(group = "stax", module = "stax-api")
                exclude(group = "stax", module = "stax")
            }
            dependencies.implementation(Libraries.Square.OkHttp.logging)
        }
        Modules.App.Domain.id -> {
            dependencies.implementation(Libraries.AniTrend.Arch.domain)
        }
        Modules.App.Navigation.id -> {
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

    dependencies.implementation(project(Modules.App.Data.path()))
    dependencies.implementation(project(Modules.App.Domain.path()))
    dependencies.implementation(project(Modules.App.Navigation.path()))
    if (!isAndroidCoreModule()) {
        dependencies.implementation(project(Modules.Android.Core.path()))
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

    dependencies.implementation(project(Modules.Android.Core.path()))
    dependencies.implementation(project(Modules.App.Core.path()))
    dependencies.implementation(project(Modules.App.Data.path()))
    dependencies.implementation(project(Modules.App.Domain.path()))
    dependencies.implementation(project(Modules.App.Navigation.path()))
}

internal fun Project.configureDependencies() {
    val dependencyStrategy = DependencyStrategy(project)
    dependencies.implementation(
        fileTree("libs") {
            include("*.jar")
        }
    )
    dependencyStrategy.applyDependenciesOn(dependencies)

    if (matchesFeatureModule()) applyFeatureModuleDependencies()
    if (isAppModule()) applyAppModuleDependencies()
    if (matchesAppModule()) applyBaseModuleDependencies()
    if (isCoreModule()) applyAndroidCoreModuleDependencies()
    if (isAndroidCoreModule()) applyAndroidCoreModuleDependencies()
    if (matchesCommonModule()) applyCommonModuleDependencies()
}