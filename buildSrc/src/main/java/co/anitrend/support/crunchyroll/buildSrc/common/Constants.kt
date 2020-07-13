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

package co.anitrend.support.crunchyroll.buildSrc.common

import org.gradle.api.Project

internal const val app = "app"
internal const val core = "app-core"
internal const val data = "app-data"
internal const val domain = "app-domain"
internal const val navigation = "app-navigation"

internal const val androidCore = "android-core"
internal const val splash = "feature-splash"
internal const val auth = "feature-auth"

internal const val catalog = "feature-catalog"
internal const val collection = "feature-collection"
internal const val discover = "feature-discover"
internal const val listing = "feature-listing"
internal const val media = "feature-media"
internal const val player = "feature-player"
internal const val series = "feature-series"
internal const val settings = "feature-settings"
internal const val search = "feature-search"
internal const val news = "feature-news"

internal val baseModules = listOf(app, core, data, domain, navigation)

internal val androidModules = listOf(
    androidCore, splash, auth
)

internal val featureModules = listOf(
    catalog, collection, discover, listing, media, player, series, settings, search, news
)

private const val coreFeatureModulePattern = "android-"
private const val featureModulePattern = "feature-"
private const val commonModulePattern = "ui-"
private const val baseModulePattern = "app-"

fun Project.isAppModule() = name == app
fun Project.isDataModule() = name == data
fun Project.isDomainModule() = name == domain
fun Project.isCoreModule() = name == core
fun Project.isNavigationModule() = name == navigation
fun Project.isAndroidCoreModule() = name == androidCore

fun Project.isBaseModule() = name.startsWith(baseModulePattern)
fun Project.isFeatureModule() = name.startsWith(featureModulePattern)
fun Project.isCoreFeatureModule() = androidModules.contains(name)
fun Project.isCommonFeatureModule() = name.startsWith(commonModulePattern)

fun Project.hasDataBindingSupport() = name == app || name == auth || name == series
fun Project.hasCoroutineSupport() = name != navigation || name != domain