/*
 *    Copyright 2019 AniTrend
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

package co.anitrend.support.crunchyroll

import android.util.Log
import co.anitrend.support.crunchyroll.core.BuildConfig
import co.anitrend.support.crunchyroll.core.CrunchyApplication
import co.anitrend.support.crunchyroll.core.analytics.AnalyticsLogger
import co.anitrend.support.crunchyroll.koin.appModules
import fr.bipi.tressence.file.FileLoggerTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import timber.log.Timber
import java.io.File

class App : CrunchyApplication() {

    /** [Koin](https://insert-koin.io/docs/2.0/getting-started/)
     *
     * Initializes dependencies for the entire application, this function is automatically called
     * in [onCreate] as the first call to assure all injections are available
     */
    override fun initializeDependencyInjection() {
        startKoin{
            androidLogger()
            androidContext(
                applicationContext
            )
            modules(appModules)
        }
    }

    /** [Koin](https://insert-koin.io/docs/2.0/getting-started/)
     *
     * Restarts the global koin instance
     */
    override fun restartDependencyInjection() {
        stopKoin()
        initializeDependencyInjection()
    }

    /**
     * Timber logging tree depending on the build type we plant the appropriate tree
     */
    override fun plantLoggingTree() {
        super.plantLoggingTree()
        val logLevel = if (BuildConfig.DEBUG) Log.VERBOSE else Log.WARN
        val file = File("${externalCacheDir?.absolutePath}/logs/").apply { mkdirs() }
        val fileLogger = FileLoggerTree.Builder()
            .withFileName("$packageName.log")
            .withDirName(file.absolutePath)
            .withSizeLimit(20_000)
            .withFileLimit(1)
            .withMinPriority(logLevel)
            .appendToFile(true)
            .build()
        Timber.plant(fileLogger)
    }
}