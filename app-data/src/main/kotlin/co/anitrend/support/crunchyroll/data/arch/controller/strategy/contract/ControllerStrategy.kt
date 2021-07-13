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

package co.anitrend.support.crunchyroll.data.arch.controller.strategy.contract

import co.anitrend.arch.data.request.callback.RequestCallback
import co.anitrend.arch.domain.entities.RequestError
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


internal abstract class ControllerStrategy<D> {

    protected val moduleTag: String = javaClass.simpleName

    /**
     * Creates human readable exceptions from a given exception
     */
    protected fun Throwable.generateForError() = when (this) {
        is UnknownHostException -> {
            RequestError(
                "Unable to resolve host",
                "Please check your internet connection and try again",
                cause
            )
        }
        is SocketTimeoutException -> {
            RequestError(
                "Connection timed out",
                "Please check your internet connection and try again",
                cause
            )
        }
        is HttpException -> {
            RequestError(
                "Server error occurred",
                message,
                cause
            )
        }
        else -> RequestError(
            "Unknown error occurred",
            message,
            cause
        )
    }

    /**
     * Execute a task under an implementation strategy
     *
     * @param callback event emitter
     * @param block what will be executed
     */
    internal abstract suspend operator fun invoke(
        callback: RequestCallback,
        block: suspend () -> D?
    ): D?
}