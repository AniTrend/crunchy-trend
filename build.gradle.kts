import java.net.URI

plugins {
    id("com.github.ben-manes.versions") version "0.29.0"
}

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath(co.anitrend.support.crunchyroll.buildSrc.Libraries.Android.Tools.buildGradle)
        classpath(co.anitrend.support.crunchyroll.buildSrc.Libraries.JetBrains.Kotlin.Gradle.plugin)

        classpath(co.anitrend.support.crunchyroll.buildSrc.Libraries.Koin.Gradle.plugin)

        classpath(co.anitrend.support.crunchyroll.buildSrc.Libraries.Google.Services.googleServices)
        classpath(co.anitrend.support.crunchyroll.buildSrc.Libraries.Google.Firebase.Crashlytics.Gradle.plugin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url = URI("https://jitpack.io") }
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(rootProject.buildDir)
    }
}

plugins.apply("koin")

tasks.named(
    "dependencyUpdates",
    com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask::class.java
).configure {
    checkForGradleUpdate = false
    outputFormatter = "json"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}