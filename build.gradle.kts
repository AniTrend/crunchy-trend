import java.net.URI

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