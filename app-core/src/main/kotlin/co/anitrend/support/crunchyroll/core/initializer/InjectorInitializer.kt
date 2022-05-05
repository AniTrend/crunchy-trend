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

package co.anitrend.support.crunchyroll.core.initializer

import android.content.Context
import androidx.startup.Initializer
import co.anitrend.support.crunchyroll.core.BuildConfig
import co.anitrend.support.crunchyroll.core.initializer.contract.AbstractInitializer
import co.anitrend.support.crunchyroll.core.koin.coreModules
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.fileProperties
import timber.log.Timber

class InjectorInitializer : AbstractInitializer<Unit>() {

    /**
     * Initializes and a component given the application [Context]
     *
     * @param context The application context.
     */
    override fun create(context: Context) {
        val logLevel = if (BuildConfig.DEBUG) Level.NONE else Level.ERROR
        startKoin {
            fragmentFactory()
            androidContext(context)
            logger(KoinLogger(logLevel))
            fileProperties("koin.properties")
            modules(coreModules)
        }
    }

    /**
     * @return A list of dependencies that this [Initializer] depends on. This is
     * used to determine initialization order of [Initializer]s.
     *
     * For e.g. if a [Initializer] `B` defines another
     * [Initializer] `A` as its dependency, then `A` gets initialized before `B`.
     */
    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(TimberInitializer::class.java)

    internal class KoinLogger(
        logLevel: Level
    ) : Logger(logLevel) {
        override fun log(level: Level, msg: MESSAGE) {
            when (level) {
                Level.DEBUG -> Timber.tag(KOIN_TAG).d(msg)
                Level.INFO -> Timber.tag(KOIN_TAG).i(msg)
                Level.ERROR -> Timber.tag(KOIN_TAG).e(msg)
                Level.NONE -> Timber.tag(KOIN_TAG).v(msg)
            }
        }
    }
}