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

package co.anitrend.support.crunchyroll.buildSrc.plugins.extensions


import groovy.lang.Closure
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.add

private enum class DependencyType(val configurationName: String) {
    API("api"),
    COMPILE("compileOnly"),
    DEBUG("debugOnly"),
    KAPT("kapt"),
    IMPLEMENTATION("implementation"),
    RUNTIME("runtimeOnly"),
    TEST("testImplementation"),
    ANDROID_TEST("androidTestImplementation")
}

private fun DependencyHandler.addDependency(
    dependencyNotation: Any,
    dependencyType: DependencyType,
    configureClosure: Closure<*>? = null
) = when (configureClosure) {
    null -> add(dependencyType.configurationName, dependencyNotation)
    else -> add(dependencyType.configurationName, dependencyNotation, configureClosure)
}

private fun DependencyHandler.addDependency(
    dependencyNotation: Any,
    dependencyType: DependencyType,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
) = add(
    dependencyType.configurationName,
    create(dependencyNotation) as ExternalModuleDependency,
    dependencyConfiguration
)

/**
 * Adds a dependency to the given configuration, and configures the dependency using the given closure.
 *
 * @param dependencyNotation The dependency notation, in one of the notations described above.
 * @param configureClosure The closure to use to configure the dependency.
 *
 * @return The dependency.
 */
internal fun DependencyHandler.kapt(
    dependencyNotation: Any,
    configureClosure: Closure<*>? = null
) = addDependency(dependencyNotation, DependencyType.KAPT, configureClosure)

/**
 * Adds a dependency to the given configuration, and configures the dependency using the given closure.
 *
 * @param dependencyNotation The dependency notation, in one of the notations described above.
 * @param configureClosure The closure to use to configure the dependency.
 *
 * @return The dependency.
 */
internal fun DependencyHandler.api(
    dependencyNotation: Any,
    configureClosure: Closure<*>? = null
) = addDependency(dependencyNotation, DependencyType.API, configureClosure)

/**
 * Adds a dependency to the given configuration, and configures the dependency using the given closure.
 *
 * @param dependencyNotation The dependency notation, in one of the notations described above.
 * @param configureClosure The closure to use to configure the dependency.
 *
 * @return The dependency.
 */
internal fun DependencyHandler.compile(
    dependencyNotation: Any,
    configureClosure: Closure<*>? = null
) = addDependency(dependencyNotation, DependencyType.COMPILE, configureClosure)

/**
 * Adds a dependency to the given configuration, and configures the dependency using the given closure.
 *
 * @param dependencyNotation The dependency notation, in one of the notations described above.
 * @param configureClosure The closure to use to configure the dependency.
 *
 * @return The dependency.
 */
internal fun DependencyHandler.debug(
    dependencyNotation: Any,
    configureClosure: Closure<*>? = null
) = addDependency(dependencyNotation, DependencyType.DEBUG, configureClosure)

/**
 * Adds a dependency to the given configuration, and configures the dependency using the given closure.
 *
 * @param dependencyNotation The dependency notation, in one of the notations described above.
 * @param configureClosure The closure to use to configure the dependency.
 *
 * @return The dependency.
 */
internal fun DependencyHandler.implementation(
    dependencyNotation: Any,
    configureClosure: Closure<*>? = null
) = addDependency(dependencyNotation, DependencyType.IMPLEMENTATION, configureClosure)

/**
 * Adds a dependency to the given configuration, and configures the dependency using the given closure.
 *
 * @param dependencyNotation The dependency notation, in one of the notations described above.
 * @param dependencyConfiguration The closure to use to configure the dependency.
 *
 * @return The dependency.
 */
internal fun DependencyHandler.implementation(
    dependencyNotation: Any,
    dependencyConfiguration: ExternalModuleDependency.() -> Unit
) = addDependency(dependencyNotation, DependencyType.IMPLEMENTATION, dependencyConfiguration)

/**
 * Adds a dependency to the given configuration, and configures the dependency using the given closure.
 *
 * @param dependencyNotation The dependency notation, in one of the notations described above.
 * @param configureClosure The closure to use to configure the dependency.
 *
 * @return The dependency.
 */
internal fun DependencyHandler.runtime(
    dependencyNotation: Any,
    configureClosure: Closure<*>? = null
) = addDependency(dependencyNotation, DependencyType.RUNTIME, configureClosure)

/**
 * Adds a dependency to the given configuration, and configures the dependency using the given closure.
 *
 * @param dependencyNotation The dependency notation, in one of the notations described above.
 * @param configureClosure The closure to use to configure the dependency.
 *
 * @return The dependency.
 */
internal fun DependencyHandler.test(
    dependencyNotation: Any,
    configureClosure: Closure<*>? = null
) = addDependency(dependencyNotation, DependencyType.TEST, configureClosure)

/**
 * Adds a dependency to the given configuration, and configures the dependency using the given closure.
 *
 * @param dependencyNotation The dependency notation, in one of the notations described above.
 * @param configureClosure The closure to use to configure the dependency.
 *
 * @return The dependency.
 */
internal fun DependencyHandler.androidTest(
    dependencyNotation: Any,
    configureClosure: Closure<*>? = null
) = addDependency(dependencyNotation, DependencyType.ANDROID_TEST, configureClosure)