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

import co.anitrend.support.crunchyroll.buildSrc.common.Versions
import co.anitrend.support.crunchyroll.buildSrc.common.hasCoroutineSupport
import co.anitrend.support.crunchyroll.buildSrc.common.isAppModule
import co.anitrend.support.crunchyroll.buildSrc.common.hasDataBindingSupport
import co.anitrend.support.crunchyroll.buildSrc.common.isBaseModule
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.baseAppExtension
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.baseExtension
import co.anitrend.support.crunchyroll.buildSrc.plugins.extensions.libraryExtension
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import java.io.File


@Suppress("UnstableApiUsage")
private fun DefaultConfig.applyAdditionalConfiguration(project: Project) {
    if (project.isAppModule())
        applicationId = "co.anitrend.support.crunchyroll"
    else
        consumerProguardFiles.add(File("consumer-rules.pro"))

    if (project.hasDataBindingSupport()) {
        println("Applying data binding feature for module -> ${project.path}")
        if (project.isAppModule())
            project.baseAppExtension().buildFeatures { dataBinding = true }
        else
            project.libraryExtension().buildFeatures { dataBinding = true }
    } else {
        println("Applying view binding feature for module -> ${project.path}")
        project.libraryExtension().buildFeatures {
            viewBinding = true
        }
    }


    if (!project.isBaseModule()) {
        println("Applying vector drawables configuration for module -> ${project.path}")
        vectorDrawables.useSupportLibrary = true
    }
}

internal fun Project.configureAndroid(): Unit = baseExtension().run {
    compileSdkVersion(Versions.compileSdk)
    defaultConfig {
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        versionCode = Versions.versionCode
        versionName = Versions.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        applyAdditionalConfiguration(project)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isTestCoverageEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isMinifyEnabled = false
            isTestCoverageEnabled = true
        }
    }

    packagingOptions {
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/*kotlin_module")
    }

    sourceSets {
        map { androidSourceSet ->
            androidSourceSet.java.srcDir(
                "src/${androidSourceSet.name}/kotlin"
            )
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    lintOptions {
        isAbortOnError = false
        isIgnoreWarnings = false
        isIgnoreTestSources = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType(KotlinCompile::class.java) {
        val compilerArgumentOptions = if (hasCoroutineSupport()) {
            listOf(
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xopt-in=kotlinx.coroutines.FlowPreview",
                "-Xopt-in=kotlinx.coroutines.FlowPreview",
                "-Xuse-experimental=kotlin.Experimental",
                "-Xopt-in=kotlin.Experimental"
            )
        } else {
            listOf(
                "-Xuse-experimental=kotlin.Experimental",
                "-Xopt-in=kotlin.Experimental"
            )
        }
        kotlinOptions {
            allWarningsAsErrors = false
            // Filter out modules that won't be using coroutines
            freeCompilerArgs = compilerArgumentOptions
        }
    }

    tasks.withType(KotlinJvmCompile::class.java) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}