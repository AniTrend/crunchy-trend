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

import androidx.startup.AppInitializer
import co.anitrend.support.crunchyroll.core.CrunchyApplication
import co.anitrend.support.crunchyroll.initializer.ApplicationInitializer
import org.koin.core.context.stopKoin
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
    override fun initializeKoin() {
        AppInitializer.getInstance(this)
            .initializeComponent(ApplicationInitializer::class.java)
    }

    /** [Koin](https://insert-koin.io/docs/2.0/getting-started/)
     *
     * Restarts the global koin instance
     */
    override fun restartDependencyInjection() {
        stopKoin()
        initializeKoin()
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG)
            createUncaughtExceptionHandler()
    }
}