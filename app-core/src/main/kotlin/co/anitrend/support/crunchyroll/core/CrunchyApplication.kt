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

package co.anitrend.support.crunchyroll.core

import android.app.Application
import co.anitrend.support.crunchyroll.core.util.theme.ThemeUtil
import coil.Coil
import coil.ImageLoader
import org.koin.android.ext.android.get

abstract class CrunchyApplication : Application() {

    /** [Koin](https://insert-koin.io/docs/2.0/getting-started/)
     *
     * Initializes dependencies for the entire application, this function is automatically called
     * in [onCreate] as the first call to assure all injections are available
     */
    protected abstract fun initializeKoin()

    /** [Koin](https://insert-koin.io/docs/2.0/getting-started/)
     *
     * Restarts the global koin instance
     */
    abstract fun restartDependencyInjection()

    private fun setupCoil() {
        val imageLoader = get<ImageLoader>()
        Coil.setImageLoader(imageLoader)
    }

    /**
     * Applies theme on application instance
     */
    protected open fun applyNightMode() {
        // apply application theme on application instance
        val themeUtil = get<ThemeUtil>()
        themeUtil.applyNightMode()
    }

    /**
     * Checks if the application needs to perform any migrations
     */
    protected open fun checkApplicationMigration() {
        //Settings(this).sharedPreferences.edit(commit = true) { clear() }
    }

    override fun onCreate() {
        super.onCreate()
        checkApplicationMigration()
        applyNightMode()
        setupCoil()
    }
}