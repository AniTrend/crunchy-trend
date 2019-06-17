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

import android.app.Application
import androidx.work.Configuration
import co.anitrend.support.crunchyroll.core.koin.crunchyCoreModules
import co.anitrend.support.crunchyroll.core.koin.crunchyCorePresenterModules
import co.anitrend.support.crunchyroll.core.koin.crunchyCoreViewModelModules
import co.anitrend.support.crunchyroll.data.koin.crunchyDataModules
import co.anitrend.support.crunchyroll.data.koin.crunchyDataNetworkModules
import co.anitrend.support.crunchyroll.data.koin.crunchyDataRepositoryModules
import co.anitrend.support.crunchyroll.data.koin.crunchyDataUseCaseModules
import co.anitrend.support.crunchyroll.koin.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application(), Configuration.Provider {

    /** [Koin](https://insert-koin.io/docs/2.0/getting-started/)
     * Initializes Koin dependency injection
     */
    private fun initializeDependencyInjection() {
        startKoin {
            androidLogger()
            androidContext(
                applicationContext
            )
            modules(
                listOf(
                    appModules,

                    crunchyCoreModules,
                    crunchyCoreViewModelModules,
                    crunchyCorePresenterModules,

                    crunchyDataModules,
                    crunchyDataUseCaseModules,
                    crunchyDataNetworkModules,
                    crunchyDataRepositoryModules
                )
            )
        }
    }

    /**
     * Timber logging tree depending on the build type we plant the appropriate tree
     */
    private fun plantLoggingTree() {
        when (BuildConfig.DEBUG) {
            true -> Timber.plant(Timber.DebugTree())
        }
    }

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     *
     *
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     *
     *
     * If you override this method, be sure to call `super.onCreate()`.
     *
     *
     * Be aware that direct boot may also affect callback order on
     * Android [android.os.Build.VERSION_CODES.N] and later devices.
     * Until the user unlocks the device, only direct boot aware components are
     * allowed to run. You should consider that all direct boot unaware
     * components, including such [android.content.ContentProvider], are
     * disabled until user unlock happens, especially when component callback
     * order matters.
     */
    override fun onCreate() {
        super.onCreate()
        initializeDependencyInjection()
        plantLoggingTree()
    }

    /**
     * @return The [Configuration] used to initialize WorkManager
     */
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .build()
    }
}