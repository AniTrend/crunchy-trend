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

import co.anitrend.support.crunchyroll.buildSrc.common.Configuration
import co.anitrend.support.crunchyroll.buildSrc.extensions.isDataModule
import co.anitrend.support.crunchyroll.buildSrc.extensions.isCoreModule
import co.anitrend.support.crunchyroll.buildSrc.extensions.isAndroidCoreModule
import co.anitrend.support.crunchyroll.buildSrc.extensions.libraryExtension
import com.android.build.api.dsl.LibraryBuildType
import com.android.build.api.dsl.LibraryDefaultConfig
import com.android.build.gradle.BaseExtension
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import java.util.*

private fun Properties.applyToBuildConfigFor(buildType: LibraryBuildType) {
    forEach { propEntry ->
        val key = propEntry.key as String
        val value = propEntry.value as String
        println("Adding build config field property -> key: $key value: $value")
        buildType.buildConfigField("String", key, value)
    }
}

private fun NamedDomainObjectContainer<LibraryBuildType>.applyVersionProperties() {
    asMap.forEach { buildTypeEntry ->
        println("Adding version build configuration fields -> ${buildTypeEntry.key}")
        val buildType = buildTypeEntry.value

        buildType.buildConfigField("String", "versionName", "\"${Configuration.versionName}\"")
        buildType.buildConfigField("int", "versionCode", Configuration.versionCode.toString())
    }
}

private fun NamedDomainObjectContainer<LibraryBuildType>.applyConfigurationProperties(project: Project) {
    asMap.forEach { buildTypeEntry ->
        println("Configuring build type -> ${buildTypeEntry.key}")
        val buildType = buildTypeEntry.value

        buildType.buildConfigField("String", "versionName", "\"${Configuration.versionName}\"")
        buildType.buildConfigField("int", "versionCode", Configuration.versionCode.toString())

        val secretsFile = project.file(".config/secrets.properties")
        if (secretsFile.exists())
            secretsFile.inputStream().use { fis ->
                Properties().run {
                    load(fis); applyToBuildConfigFor(buildType)
                }
            }

        val configurationFile = project.file(".config/configuration.properties")
        if (configurationFile.exists())
            configurationFile.inputStream().use { fis ->
                Properties().run {
                    load(fis); applyToBuildConfigFor(buildType)
                }
            }
    }
}

private fun LibraryDefaultConfig.applyRoomCompilerOptions(project: Project) {
    println("Adding java compiler options for room on module-> ${project.path}")
    javaCompileOptions {
        annotationProcessorOptions {
            arguments(
                mapOf(
                    "room.schemaLocation" to "${project.projectDir}/schemas",
                    "room.expandingProjections" to "true",
                    "room.incremental" to "true"
                )
            )
        }
    }
}

internal fun Project.createSigningConfiguration(extension: BaseExtension) {
    var properties: Properties? = null
    val keyStoreFile = project.file(".config/keystore.properties")
    if (keyStoreFile.exists())
        keyStoreFile.inputStream().use { fis ->
            Properties().run {
                load(fis);
                properties = this
            }
        }
    else println("${keyStoreFile.absolutePath} could not be found, automated releases may not be singed")
    properties?.also {
        extension.signingConfigs {
            create("release") {
                storeFile(file(it["STORE_FILE"] as String))
                storePassword(it["STORE_PASSWORD"] as String)
                keyAlias(it["STORE_KEY_ALIAS"] as String)
                keyPassword(it["STORE_KEY_PASSWORD"] as String)
            }
        }
    }
}

internal fun Project.configureOptions() {
    if (isDataModule()) {
        libraryExtension().run {
            defaultConfig {
                applyRoomCompilerOptions(this@configureOptions)
            }
            buildTypes {
                applyConfigurationProperties(this@configureOptions)
            }
        }
    }

    if (isCoreModule() || isAndroidCoreModule()) {
        libraryExtension().run {
            buildTypes {
                applyVersionProperties()
            }
        }
    }
}