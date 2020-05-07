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
import co.anitrend.support.crunchyroll.core.CrunchyApplication
import co.anitrend.support.crunchyroll.core.helper.StorageHelper
import co.anitrend.support.crunchyroll.koin.appModules
import fr.bipi.tressence.file.FileLoggerTree
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : CrunchyApplication() {

    private fun createUncaughtExceptionHandler() {
        val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            Timber.tag(t.name).e(e)
            exceptionHandler?.uncaughtException(t, e)
        }
    }

    /** [Koin](https://insert-koin.io/docs/2.0/getting-started/)
     *
     * Initializes dependencies for the entire application, this function is automatically called
     * in [onCreate] as the first call to assure all injections are available
     */
    override fun initializeDependencyInjection() {
        startKoin{
            fragmentFactory()
            androidLogger(
                level = if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR
            )
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
        val file = StorageHelper.getLogsCache(applicationContext)
        val logSizeLimit = 750 * 1024
        val fileLogger = FileLoggerTree.Builder()
            .withFileName("$packageName.log")
            .withDirName(file.absolutePath)
            .withSizeLimit(logSizeLimit)
            .withFileLimit(1)
            .withMinPriority(logLevel)
            .appendToFile(true)
            .build()
        Timber.plant(fileLogger)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG)
            createUncaughtExceptionHandler()
    }
}