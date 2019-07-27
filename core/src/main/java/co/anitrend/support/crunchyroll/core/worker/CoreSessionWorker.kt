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

package co.anitrend.support.crunchyroll.core.worker

import android.content.Context
import androidx.work.WorkerParameters
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.data.usecase.session.CrunchySessionUseCase
import io.wax911.support.core.worker.SupportCoroutineWorker
import org.koin.core.KoinComponent
import org.koin.core.inject

class CoreSessionWorker(
    context: Context,
    workerParameters: WorkerParameters
) : SupportCoroutineWorker<CrunchyCorePresenter>(context, workerParameters), KoinComponent {

    override val presenter by inject<CrunchyCorePresenter>()
    private val crunchySessionUseCase by inject<CrunchySessionUseCase>()

    /**
     * A suspending method to do your work.  This function runs on the coroutine context specified
     * by [coroutineContext].
     *
     * A CoroutineWorker is given a maximum of ten minutes to finish its execution and return a
     * [androidx.work.ListenableWorker.Result].  After this time has expired, the worker will be signalled to
     * stop.
     *
     * @return The [androidx.work.ListenableWorker.Result] of the result of the background work; note that
     * dependent work will not execute if you return [androidx.work.ListenableWorker.Result.failure]
     */
    override suspend fun doWork(): Result {
        val payload = when (presenter.supportPreference.isAuthenticated) {
            true -> {
                CrunchySessionUseCase.Payload(
                    userId = presenter.supportPreference.authenticatedUserId,
                    sessionType = CrunchySessionUseCase.Payload.RequestType.START_UNBLOCK_SESSION
                )
            }
            else -> {
                CrunchySessionUseCase.Payload(
                    sessionType = CrunchySessionUseCase.Payload.RequestType.START_CORE_SESSION
                )
            }
        }

        val networkState = crunchySessionUseCase(payload)

        if (networkState.isLoaded())
            return Result.success()
        return Result.failure()
    }

    companion object {
        const val TAG = "co.anitrend.support.crunchyroll.core.worker:CoreSessionWorker"
    }
}