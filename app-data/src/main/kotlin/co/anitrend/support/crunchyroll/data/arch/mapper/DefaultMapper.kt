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

package co.anitrend.support.crunchyroll.data.arch.mapper

import co.anitrend.arch.data.mapper.SupportResponseMapper
import co.anitrend.support.crunchyroll.data.arch.railway.OutCome
import co.anitrend.support.crunchyroll.data.arch.railway.extension.evaluate
import co.anitrend.support.crunchyroll.data.arch.railway.extension.otherwise
import co.anitrend.support.crunchyroll.data.arch.railway.extension.then
import timber.log.Timber


/**
 * Making it easier for us to implement error logging and provide better error messages
 */
internal abstract class DefaultMapper<S, D> : SupportResponseMapper<S, D>() {

    /**
     * Save [data] into your desired local source
     */
    protected abstract suspend fun persist(data: D)

    /**
     * Handles the persistence of [data] into a local source
     *
     * @return [OutCome.Pass] or [OutCome.Fail] of the operation
     */
    protected suspend fun persistChanges(data: D): OutCome<Nothing?> {
        return runCatching {
            persist(data)
            OutCome.Pass(null)
        }.getOrElse { OutCome.Fail(listOf(it)) }
    }

    /**
     * If [data] is a type of [Collection], checks if empty otherwise checks nullability
     */
    protected fun <T> checkValidity(data: T?): OutCome<T> {
        if (data == null) {
            Timber.tag(moduleTag).v("Data is null")
            return OutCome.Fail(emptyList())
        }
        if (data is Collection<*> && data.isEmpty()) {
            Timber.tag(moduleTag).v("Data is empty")
            return OutCome.Fail(emptyList())
        }
        return OutCome.Pass(data)
    }

    /**
     * Logs [exceptions] of failed operations
     */
    protected fun handleException(exceptions: List<Throwable>) {
        exceptions.forEach {
            Timber.tag(moduleTag).w(it)
        }
    }

    /**
     * Inserts the given object into the implemented room database,
     *
     * @param mappedData mapped object from [onResponseMapFrom] to insert into the database
     */
    override suspend fun onResponseDatabaseInsert(mappedData: D) {
        mappedData evaluate
                ::checkValidity then
                ::persistChanges otherwise
                ::handleException
    }
}