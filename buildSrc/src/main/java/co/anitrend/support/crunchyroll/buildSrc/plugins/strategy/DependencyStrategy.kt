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

package co.anitrend.support.crunchyroll.buildSrc.plugins.strategy

import co.anitrend.support.crunchyroll.buildSrc.Libraries
import co.anitrend.support.crunchyroll.buildSrc.common.*
import co.anitrend.support.crunchyroll.buildSrc.common.core
import co.anitrend.support.crunchyroll.buildSrc.common.data
import co.anitrend.support.crunchyroll.buildSrc.common.domain
import co.anitrend.support.crunchyroll.buildSrc.common.navigation
import org.gradle.api.artifacts.dsl.DependencyHandler

internal class DependencyStrategy(
    private val module: String
) {
    private fun DependencyHandler.applyDefaultDependencies() {
        add("implementation", Libraries.JetBrains.Kotlin.stdlib)
        if (module != domain)
            add("implementation", Libraries.timber)

        add("testImplementation", Libraries.junit)
        add("testImplementation", Libraries.mockk)
    }

    private fun DependencyHandler.applyAndroidTestDependencies() {
        add("androidTestImplementation", Libraries.AndroidX.Test.coreKtx)
        add("androidTestImplementation", Libraries.AndroidX.Test.rules)
        add("androidTestImplementation", Libraries.AndroidX.Test.runner)
        add("androidTestImplementation", Libraries.AndroidX.Test.Extension.junitKtx)
    }

    private fun DependencyHandler.applyLifeCycleDependencies() {
        add("implementation", Libraries.AndroidX.Lifecycle.liveDataCoreKtx)
        add("implementation", Libraries.AndroidX.Lifecycle.runTimeKtx)
        add("implementation", Libraries.AndroidX.Lifecycle.liveDataKtx)
        add("implementation", Libraries.AndroidX.Lifecycle.extensions)
    }

    private fun DependencyHandler.applyCoroutinesDependencies() {
        add("implementation", Libraries.JetBrains.Coroutines.android)
        add("implementation", Libraries.JetBrains.Coroutines.core)
        add("testImplementation", Libraries.JetBrains.Coroutines.test)
    }

    private fun DependencyHandler.applyKoinDependencies() {
        add("implementation", Libraries.Koin.core)
        add("implementation", Libraries.Koin.extension)
        add("testImplementation", Libraries.Koin.test)
        if (module != data || module != core || module != androidCore) {
            add("implementation", Libraries.Koin.AndroidX.scope)
            add("implementation", Libraries.Koin.AndroidX.fragment)
            add("implementation", Libraries.Koin.AndroidX.viewmodel)
        }
    }

    private fun DependencyHandler.applyOtherDependencies() {
        when (module) {
            app -> {
                add("implementation", Libraries.AniTrend.Arch.recycler)
                add("implementation", Libraries.AniTrend.Arch.domain)
                add("implementation", Libraries.AniTrend.Arch.theme)
                add("implementation", Libraries.AniTrend.Arch.core)
                add("implementation", Libraries.AniTrend.Arch.data)
                add("implementation", Libraries.AniTrend.Arch.ext)
                add("implementation", Libraries.AniTrend.Arch.ui)
            }
            core -> {
                add("implementation", Libraries.AniTrend.Arch.recycler)
                add("implementation", Libraries.AniTrend.Arch.domain)
                add("implementation", Libraries.AniTrend.Arch.theme)
                add("implementation", Libraries.AniTrend.Arch.core)
                add("implementation", Libraries.AniTrend.Arch.data)
                add("implementation", Libraries.AniTrend.Arch.ext)
                add("implementation", Libraries.AniTrend.Arch.ui)
            }
            data -> {
                add("implementation", Libraries.AniTrend.Arch.domain)
                add("implementation", Libraries.AniTrend.Arch.data)
                add("implementation", Libraries.AniTrend.Arch.ext)
            }
        }
    }

    fun applyDependenciesOn(handler: DependencyHandler) {
        handler.applyDefaultDependencies()
        if (module != domain || module != navigation) {
            handler.applyLifeCycleDependencies()
            handler.applyAndroidTestDependencies()
            handler.applyCoroutinesDependencies()
            handler.applyKoinDependencies()
            handler.applyOtherDependencies()
        }
    }
}