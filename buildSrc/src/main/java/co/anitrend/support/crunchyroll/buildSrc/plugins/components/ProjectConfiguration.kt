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

import co.anitrend.support.crunchyroll.buildSrc.common.*
import co.anitrend.support.crunchyroll.buildSrc.extensions.isAppModule
import co.anitrend.support.crunchyroll.buildSrc.extensions.isCoreModule
import co.anitrend.support.crunchyroll.buildSrc.extensions.isNavigationModule
import co.anitrend.support.crunchyroll.buildSrc.extensions.matchesAppModule
import co.anitrend.support.crunchyroll.buildSrc.extensions.hasDataBindingSupport
import co.anitrend.support.crunchyroll.buildSrc.extensions.hasCoroutineSupport
import co.anitrend.support.crunchyroll.buildSrc.extensions.baseAppExtension
import co.anitrend.support.crunchyroll.buildSrc.extensions.baseExtension
import co.anitrend.support.crunchyroll.buildSrc.extensions.libraryExtension
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import java.io.File


private fun Project.configureBuildFlavours() {
    baseAppExtension().run {
        flavorDimensions.add("default")
        productFlavors {
            create("google") {
                dimension = "default"
                isDefault = true
            }
        }
        applicationVariants.all {
            outputs.map { it as BaseVariantOutputImpl }.forEach { output ->
                val original = output.outputFileName
                output.outputFileName = original
            }
        }
    }
}

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


    if (!project.matchesAppModule()) {
        println("Applying vector drawables configuration for module -> ${project.path}")
        vectorDrawables.useSupportLibrary = true
    }
}

internal fun Project.configureAndroid(): Unit = baseExtension().run {
    compileSdkVersion(Versions.compileSdk)
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = Versions.versionCode
        versionName = Versions.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        applyAdditionalConfiguration(project)
    }


    if (isAppModule()) {
        project.configureBuildFlavours()
        project.createSigningConfiguration(this)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isTestCoverageEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (project.file(".config/keystore.properties").exists())
                signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            isTestCoverageEnabled = true
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    packagingOptions {
        excludes.add("META-INF/NOTICE.txt")
        excludes.add("META-INF/LICENSE")
        excludes.add("META-INF/LICENSE.txt")
        // Exclude potential duplicate kotlin_module files
        excludes.add("META-INF/*kotlin_module")
        // Exclude consumer proguard files
        excludes.add("META-INF/proguard/*")
        // Exclude AndroidX version files
        excludes.add("META-INF/*.version")
        // Exclude the Firebase/Fabric/other random properties files
        excludes.add("META-INF/*.properties")
        excludes.add("/*.properties")
        excludes.add("fabric/*.properties")
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

    tasks.withType(KotlinJvmCompile::class.java) {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    tasks.withType(KotlinCompile::class.java) {
        val compilerArgumentOptions = mutableListOf(
            "-Xuse-experimental=kotlin.Experimental",
            "-Xopt-in=kotlin.ExperimentalStdlibApi",
            "-Xopt-in=kotlin.Experimental"
        )

        if (hasCoroutineSupport()) {
            compilerArgumentOptions.add("-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
            compilerArgumentOptions.add("-Xopt-in=kotlinx.coroutines.FlowPreview")
        }

        if (isAppModule() || isCoreModule() || isNavigationModule()) {
            compilerArgumentOptions.apply {
                add("-Xopt-in=org.koin.core.component.KoinApiExtension")
                add("-Xopt-in=org.koin.core.KoinExperimentalAPI")
            }
        }

        kotlinOptions {
            allWarningsAsErrors = false
            // Filter out modules that won't be using coroutines
            freeCompilerArgs = compilerArgumentOptions
        }
    }
}