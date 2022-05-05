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
import android.util.Log
import co.anitrend.support.crunchyroll.core.BuildConfig
import co.anitrend.support.crunchyroll.core.helper.StorageHelper
import co.anitrend.support.crunchyroll.core.initializer.contract.AbstractInitializer
import fr.bipi.tressence.file.FileLoggerTree
import timber.log.Timber
import java.io.IOException

class TimberInitializer : AbstractInitializer<Unit>() {

    @Throws(IOException::class)
    private fun createFileLoggingTree(context: Context): Timber.Tree {
        val loggingFile = StorageHelper.getLogsCache(context)
        return FileLoggerTree.Builder()
            .withFileName("${context.packageName}.log")
            .withDirName(loggingFile.absolutePath)
            .withSizeLimit(logSizeLimit)
            .withFileLimit(1)
            .withMinPriority(logLevel)
            .appendToFile(true)
            .build()
    }

    /**
     * Initializes and a component given the application [Context]
     *
     * @param context The application context.
     */
    override fun create(context: Context) {
        val fileLoggerTree = createFileLoggingTree(context)
        Timber.plant(fileLoggerTree)
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

    companion object {
        val logLevel = if (BuildConfig.DEBUG) Log.VERBOSE else Log.WARN
        const val logSizeLimit = 850 * 1024
    }
}