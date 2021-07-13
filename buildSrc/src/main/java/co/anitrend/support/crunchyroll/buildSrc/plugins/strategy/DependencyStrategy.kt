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
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.implementation
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.androidTest
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.test
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.kapt
import org.gradle.api.artifacts.dsl.DependencyHandler

internal class DependencyStrategy(
    private val module: String
) {
    private fun DependencyHandler.applyDefaultDependencies() {
        implementation(Libraries.JetBrains.Kotlin.stdlib)
        if (module != domain)
            implementation(Libraries.timber)

        test(Libraries.junit)
        test(Libraries.mockk)
    }

    private fun DependencyHandler.applyAndroidTestDependencies() {
        androidTest(Libraries.AndroidX.Test.coreKtx)
        androidTest(Libraries.AndroidX.Test.rules)
        androidTest(Libraries.AndroidX.Test.runner)
        androidTest(Libraries.AndroidX.Test.Espresso.core)
        androidTest(Libraries.AndroidX.Test.Extension.junitKtx)
        androidTest(Libraries.mockk)
    }

    private fun DependencyHandler.applyLifeCycleDependencies() {
        implementation(Libraries.AndroidX.Lifecycle.liveDataCoreKtx)
        implementation(Libraries.AndroidX.Lifecycle.runTimeKtx)
        implementation(Libraries.AndroidX.Lifecycle.liveDataKtx)
        implementation(Libraries.AndroidX.Lifecycle.extensions)
    }

    private fun DependencyHandler.applyCoroutinesDependencies() {
        implementation(Libraries.JetBrains.KotlinX.Coroutines.android)
        implementation(Libraries.JetBrains.KotlinX.Coroutines.core)
        test(Libraries.JetBrains.KotlinX.Coroutines.test)
        androidTest(Libraries.CashApp.Turbine.turbine)
        test(Libraries.CashApp.Turbine.turbine)
    }

    private fun DependencyHandler.applyKoinDependencies() {
        implementation(Libraries.Koin.core)
        implementation(Libraries.Koin.extension)
        test(Libraries.Koin.test)
        if (module != data || module != core || module != androidCore) {
            implementation(Libraries.Koin.AndroidX.scope)
            implementation(Libraries.Koin.AndroidX.fragment)
            implementation(Libraries.Koin.AndroidX.viewModel)
            implementation(Libraries.Koin.AndroidX.workManager)
        }
    }

    private fun DependencyHandler.applyOtherDependencies() {
        when (module) {
            app -> {
                implementation(Libraries.AniTrend.Arch.recycler)
                implementation(Libraries.AniTrend.Arch.domain)
                implementation(Libraries.AniTrend.Arch.theme)
                implementation(Libraries.AniTrend.Arch.core)
                implementation(Libraries.AniTrend.Arch.data)
                implementation(Libraries.AniTrend.Arch.ext)
                implementation(Libraries.AniTrend.Arch.ui)
            }
            core -> {
                implementation(Libraries.AniTrend.Arch.recycler)
                implementation(Libraries.AniTrend.Arch.domain)
                implementation(Libraries.AniTrend.Arch.theme)
                implementation(Libraries.AniTrend.Arch.core)
                implementation(Libraries.AniTrend.Arch.data)
                implementation(Libraries.AniTrend.Arch.ext)
                implementation(Libraries.AniTrend.Arch.ui)
            }
            data -> {
                implementation(Libraries.AniTrend.Arch.domain)
                implementation(Libraries.AniTrend.Arch.data)
                implementation(Libraries.AniTrend.Arch.ext)
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