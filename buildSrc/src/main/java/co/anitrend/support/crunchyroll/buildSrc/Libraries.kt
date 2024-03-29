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

package co.anitrend.support.crunchyroll.buildSrc

import co.anitrend.support.crunchyroll.buildSrc.common.Versions

object Libraries {

    const val threeTenBp = "com.jakewharton.threetenabp:threetenabp:${Versions.threeTenBp}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    const val treessence = "com.github.bastienpaulfr:Treessence:${Versions.treesSence}"
    const val debugDb = "com.amitshekhar.android:debug-db:${Versions.debugDB}"

    const val junit = "junit:junit:${Versions.junit}"

    const val jsoup = "org.jsoup:jsoup:${Versions.jsoup}"

    const val scalingImageView = "com.davemorrissey.labs:subsampling-scale-image-view-androidx:${Versions.scalingImageView}"

    const val retrofitSerializer = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.serializationConverter}"

    const val deeplink = "com.hellofresh.android:deeplink:${Versions.deeplink}"

    object Repositories {
        const val jitPack = "https://www.jitpack.io"
        const val dependencyUpdates = "https://dl.bintray.com/pdvrieze/maven"
        const val accompanistSnapshot = "https://oss.sonatype.org/content/repositories/snapshots"
    }

    object Android {

        object Tools {
            private const val version = "7.3.0"
            const val buildGradle = "com.android.tools.build:gradle:${version}"
        }
    }

    object AndroidX {

        object Activity {
            private const val version = "1.5.0"
            const val activityKtx = "androidx.activity:activity-ktx:$version"

            object Compose {
                const val activityCompose = "androidx.activity:activity-compose:$version"
            }
        }

        object Collection {
            private const val version = "1.2.0"
            const val collection = "androidx.collection:collection:$version"
            const val collectionKtx = "androidx.collection:collection-ktx:$version"
        }

        object Compose {
            internal const val version = "1.2.1"

            object Compiler {
                internal const val version = "1.3.1"
            }

            object Foundation {
                const val foundation = "androidx.compose.foundation:foundation:$version"
                const val layout = "androidx.compose.foundation:foundation-layout:$version"
            }

            object Material {
                const val material = "androidx.compose.material3:material3:1.0.0-beta03"
                const val ripple = "androidx.compose.material:material-ripple:$version"

                object Icons {
                    const val core = "androidx.compose.material:material-icons-core:$version"
                    const val extended = "androidx.compose.material:material-icons-extended:$version"
                }
            }

            object Runtime {
                const val runtime = "androidx.compose.runtime:runtime:$version"
                const val liveData = "androidx.compose.runtime:runtime-livedata:$version"
            }

            object Ui {
                const val ui = "androidx.compose.ui:ui:$version"
                const val tooling = "androidx.compose.ui:ui-tooling:$version"
                const val test = "androidx.compose.ui:ui-test:$version"
                const val viewBinding = "androidx.compose.ui:ui-viewbinding:$version"
            }
        }

        object Core {
            private const val version = "1.9.0"
            const val core = "androidx.core:core:$version"
            const val coreKtx = "androidx.core:core-ktx:$version"

            object Animation {
                private const val version = "1.0.0-alpha01"
                const val animation = "androidx.core:core-animation:${version}"
                const val animationTest = "androidx.core:core-animation-testing:${version}"
            }
        }

        object ConstraintLayout {
            private const val version = "2.1.4"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:$version"

            object Compose {
                private const val version = "1.0.1"
                const val constraintLayoutCompose = "androidx.constraintlayout:constraintlayout-compose:$version"
            }
        }

        object Fragment {
            private const val version = "1.5.2"
            const val fragment = "androidx.fragment:fragment:$version"
            const val fragmentKtx = "androidx.fragment:fragment-ktx:$version"
            const val test = "androidx.fragment:fragment-ktx:fragment-testing$version"
        }

        object Glance {
            private const val version = "1.0.0-alpha03"
            const val glance = "androidx.glance:glance:$version"
            const val widget = "androidx.glance:glance-appwidget:$version"
            const val proto = "androidx.glance:glance-appwidget-proto:$version"
        }

        object Lifecycle {
            private const val version = "2.5.1"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
            const val runTimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
            const val liveDataCoreKtx = "androidx.lifecycle:lifecycle-livedata-core-ktx:$version"

            object Compose {
                private const val version = "2.5.1"
                const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
            }
        }

        @Deprecated("Use Media2 instead")
        object Media {
            private const val version = "1.1.0"
            const val media = "androidx.media:media:$version"
        }

        object Media2 {
            private const val version = "1.0.0-alpha04"
            const val media2 = "androidx.media2:media2:$version"

            object Common {
                private const val version = "1.1.0-alpha01"
                const val common = "androidx.media2:media2-common:${version}"
            }

            object ExoPlayer {
                private const val version = "1.1.0-alpha01"
                const val exoPlayer = "androidx.media2:media2-exoplayer:${version}"
            }

            object Player {
                private const val version = "1.1.0-alpha01"
                const val player = "androidx.media2:media2-player:${version}"
            }

            object Session {
                private const val version = "1.1.0-alpha01"
                const val session = "androidx.media2:media2-session:${version}"
            }

            object Widget {
                private const val version = "1.1.0-alpha01"
                const val widget = "androidx.media2:media2-widget:${version}"
            }
        }

        object Paging {
            private const val version = "2.1.2"
            const val common = "androidx.paging:paging-common-ktx:$version"
            const val runtime = "androidx.paging:paging-runtime:$version"
            const val runtimeKtx = "androidx.paging:paging-runtime-ktx:$version"

            object Compose {
                private const val version = "1.0.0-alpha08"
                const val pagingCompose = "androidx.paging:paging-compose:$version"
            }
        }

        object Palette {
            private const val version = "1.0.0"
            const val palette = "androidx.palette:palette:$version"
            const val paletteKtx = "androidx.palette:palette-ktx:$version"
        }

        object Preference {
            private const val version = "1.2.0"
            const val preference = "androidx.preference:preference:$version"
            const val preferenceKtx = "androidx.preference:preference-ktx:$version"
        }

        object Recycler {
            private const val version = "1.3.0-alpha02"
            const val recyclerView = "androidx.recyclerview:recyclerview:$version"

            object Selection {
                private const val version = "1.1.0-rc03"
                const val selection = "androidx.recyclerview:recyclerview-selection:$version"
            }
        }

        object Room {
            private const val version = "2.4.3"
            const val compiler = "androidx.room:room-compiler:$version"
            const val runtime = "androidx.room:room-runtime:$version"
            const val test = "androidx.room:room-testing:$version"
            const val ktx = "androidx.room:room-ktx:$version"
        }

        object StartUp {
            private const val version = "1.1.1"
            const val startUpRuntime = "androidx.startup:startup-runtime:$version"
        }

        object SwipeRefresh {
            private const val version = "1.2.0-alpha01"
            const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:$version"
        }

        object Test {
            private const val version = "1.4.0"
            const val core = "androidx.test:core:$version"
            const val coreKtx = "androidx.test:core-ktx:$version"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            object Espresso {
                private const val version = "3.4.0"
                const val core = "androidx.test.espresso:espresso-core:$version"
            }

            object Extension {
                private const val version = "1.1.3"
                const val junit = "androidx.test.ext:junit:$version"
                const val junitKtx = "androidx.test.ext:junit-ktx:$version"
            }
        }

        object Work {
            private const val version = "2.7.1"
            const val runtimeKtx = "androidx.work:work-runtime-ktx:$version"
            const val runtime = "androidx.work:work-runtime:$version"
            const val test = "androidx.work:work-test:$version"
        }
    }

    object AniTrend {

        object Arch {
			private const val version = "1.4.1-alpha01"
            const val ui = "com.github.anitrend.support-arch:ui:${version}"
            const val core = "com.github.anitrend.support-arch:core:${version}"
            const val data = "com.github.anitrend.support-arch:data:${version}"
            const val ext = "com.github.anitrend.support-arch:extension:${version}"
            const val theme = "com.github.anitrend.support-arch:theme:${version}"
            const val domain = "com.github.anitrend.support-arch:domain:${version}"
            const val recycler = "com.github.anitrend.support-arch:recycler:${version}"
        }

        object Material {
            private const val version = "0.2.0"
            const val multiSearch = "com.github.anitrend:material-multi-search:${version}"
        }

        object QueryBuilder {
            private const val version = "0.1.4-alpha01"
            const val core = "com.github.anitrend.support-query-builder:core:$version"
            const val annotation = "com.github.anitrend.support-query-builder:annotations:$version"
            const val processor = "com.github.anitrend.support-query-builder:processor:$version"
        }
    }

    object AirBnB {
        object Paris {
            private const val version = "2.0.1"
            const val paris = "com.airbnb.android:paris:$version"
            /** if using annotations */
            const val processor = "com.airbnb.android:paris-processor:$version"
        }
    }

    object Blitz {
        private const val version = "1.0.10"
        const val blitz = "com.github.perfomer:blitz:$version"
    }

    object CashApp {
        object Copper {
            private const val version = "1.0.0"
            const val copper = "app.cash.copper:copper-flow:$version"
        }

        object Contour {
            private const val version = "1.1.0"
            const val contour = "app.cash.contour:contour:$version"
        }

        object Turbine {
            private const val version = "0.10.0"
            const val turbine = "app.cash.turbine:turbine:$version"
        }
    }

    object Chuncker {
        private const val version = "3.5.2"

        const val debug = "com.github.ChuckerTeam.Chucker:library:$version"
        const val release = "com.github.ChuckerTeam.Chucker:library-no-op:$version"
    }

    object Coil {
        private const val version = "1.4.0"
        const val coil = "io.coil-kt:coil:$version"
        const val base = "io.coil-kt:coil-base:$version"
        const val gif = "io.coil-kt:coil-gif:$version"
        const val svg = "io.coil-kt:coil-svg:$version"
        const val video = "io.coil-kt:coil-video:$version"
        const val compose = "io.coil-kt:coil-compose:$version"
    }

    object Google {

        object Accompanist {
            private const val version = "0.25.1"
            const val insets = "com.google.accompanist:accompanist-insets:$version"
            const val uiController = "com.google.accompanist:accompanist-systemuicontroller:${version}"
            const val appCompatTheme = "com.google.accompanist:accompanist-appcompat-theme:${version}"
            const val pager = "com.google.accompanist:accompanist-pager:${version}"
            const val pagerIndicators = "com.google.accompanist:accompanist-pager-indicators:${version}"
            const val flowLayout = "com.google.accompanist:accompanist-flowlayout:${version}"
        }

        object Exo {
            private const val version = "2.12.2"
            const val workManager = "com.google.android.exoplayer:extension-workmanager:$version"
            const val okHttp = "com.google.android.exoplayer:extension-okhttp:$version"
            const val core = "com.google.android.exoplayer:extension-core:$version"
            const val dash = "com.google.android.exoplayer:extension-dash:$version"
            const val hls = "com.google.android.exoplayer:exoplayer-hls:$version"
            const val ui = "com.google.android.exoplayer:exoplayer-ui:$version"
        }

        object Firebase {
            private const val version = "17.4.4"
            const val firebaseCore = "com.google.firebase:firebase-core:$version"

            object Analytics {
                private const val version = "21.1.1"
                const val analytics = "com.google.firebase:firebase-analytics:$version"
                const val analyticsKtx = "com.google.firebase:firebase-analytics-ktx:$version"
            }

            object Crashlytics {
                private const val version = "18.2.13"
                const val crashlytics = "com.google.firebase:firebase-crashlytics:$version"

                object Gradle {
                    private const val version = "2.9.2"
                    const val plugin = "com.google.firebase:firebase-crashlytics-gradle:$version"
                }
            }
        }

        object FlexBox {
            private const val version = "2.0.1"
            const val flexBox = "com.google.android:flexbox:$version"
        }

        object Material {
            private const val version = "1.6.1"
            const val material = "com.google.android.material:material:$version"

            object Compose {
                private const val version = "1.0.20"
                const val themeAdapter = "com.google.android.material:compose-theme-adapter-3:$version"
            }
        }

        object Services {
            private const val version = "4.3.14"
            const val googleServices = "com.google.gms:google-services:$version"
        }
    }

    object JetBrains {
        object Kotlin {
            internal const val version = "1.7.20"
            const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
            const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"

            object Gradle {
                const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
            }

            object Android {
                const val extensions = "org.jetbrains.kotlin:kotlin-android-extensions:$version"
            }

            object Serialization {
                const val serialization = "org.jetbrains.kotlin:kotlin-serialization:$version"
            }
        }

        object KotlinX {
            object Coroutines {
                private const val version = "1.6.4"
                const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
                const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
                const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
            }

            object Serialization {
                private const val version = "1.3.3"
                const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
            }
        }
    }

    object Koin {
        private const val version = "3.1.6"
        const val android = "io.insert-koin:koin-android:$version"
        const val core = "io.insert-koin:koin-core:$version"

        object AndroidX {
            const val compose = "io.insert-koin:koin-androidx-compose:$version"
            const val navigation = "io.insert-koin:koin-androidx-navigation:$version"
            const val workManager = "io.insert-koin:koin-androidx-workmanager:$version"
        }

        object Gradle {
            const val plugin = "io.insert-koin:koin-gradle-plugin:$version"
        }

        object Test {
            const val test = "io.insert-koin:koin-test:$version"
            const val testJUnit4 = "io.insert-koin:koin-test-junit4:$version"
        }
    }

    object Markwon {
        private const val version = "4.6.2"
        const val core = "io.noties.markwon:core:$version"
        const val html = "io.noties.markwon:html:$version"
        const val image = "io.noties.markwon:image:$version"
        const val coil = "io.noties.markwon:image-coil:$version"
        const val parser = "io.noties.markwon:inline-parser:$version"
        const val linkify = "io.noties.markwon:linkify:$version"
        const val simpleExt = "io.noties.markwon:simple-ext:$version"
        const val syntaxHighlight = "io.noties.markwon:syntax-highlight:$version"

        object Extension {
            const val taskList = "io.noties.markwon:ext-tasklist:$version"
            const val strikeThrough = "io.noties.markwon:ext-strikethrough:$version"
            const val tables = "io.noties.markwon:ext-tables:$version"
            const val latex = "io.noties.markwon:ext-latex:$version"
        }
    }

    object Mockk {
        const val version = "1.12.8"
        const val mockk = "io.mockk:mockk:$version"
        const val mockkAndroid = "io.mockk:mockk-android:$version"
    }

    object MaterialDialogs {
        private const val version = "3.3.0"
        const val core = "com.afollestad.material-dialogs:core:$version"
        const val input = "com.afollestad.material-dialogs:input:$version"
        const val files = "com.afollestad.material-dialogs:files:$version"
        const val colour = "com.afollestad.material-dialogs:colour:$version"
        const val dateTime = "com.afollestad.material-dialogs:dateTime:$version"
        const val lifecycle = "com.afollestad.material-dialogs:lifecycle:$version"
        const val bottomsheets = "com.afollestad.material-dialogs:bottomsheets:$version"
    }

    object Saket {

        object BetterLinkMovement {
            private const val version = "2.2.0"
            const val betterLinkMovement = "me.saket:better-link-movement-method:$version"
        }
    }

    object Sheets {
        private const val version = "2.1.3"

        const val calendar = "com.maxkeppeler.sheets:calendar:$version"
        const val core = "com.maxkeppeler.sheets:core:$version"
        const val info = "com.maxkeppeler.sheets:info:$version"
        const val input = "com.maxkeppeler.sheets:input:$version"
        const val lottie = "com.maxkeppeler.sheets:lottie:$version"
        const val options = "com.maxkeppeler.sheets:options:$version"
        const val simple = "com.maxkeppeler.sheets:simple:$version"
        const val timeClock = "com.maxkeppeler.sheets:time-clock:$version"
        const val time = "com.maxkeppeler.sheets:time:$version"
    }

    object Square {
        object LeakCanary {
            private const val version = "2.8.1"
            const val leakCanary = "com.squareup.leakcanary:leakcanary-android:$version"
        }

        object Retrofit {
            private const val version = "2.9.0"
            const val retrofit = "com.squareup.retrofit2:retrofit:$version"
            const val gsonConverter =  "com.squareup.retrofit2:converter-gson:$version"
            const val xmlConverter =  "com.squareup.retrofit2:converter-simplexml:$version"
        }

        object OkHttp {
            private const val version = "4.9.2"
            const val okhttp = "com.squareup.okhttp3:okhttp:$version"
            const val logging = "com.squareup.okhttp3:logging-interceptor:$version"
            const val mockServer = "com.squareup.okhttp3:mockwebserver:$version"
        }
    }
}
