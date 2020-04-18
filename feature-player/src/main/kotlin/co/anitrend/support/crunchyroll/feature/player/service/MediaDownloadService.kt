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

package co.anitrend.support.crunchyroll.feature.player.service

import android.app.Notification
import android.content.Context
import com.google.android.exoplayer2.ext.workmanager.WorkManagerScheduler
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.Requirements
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import org.koin.core.KoinComponent
import org.koin.core.get

class MediaDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID_NONE,
    //FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    0,
    0
), KoinComponent {

    /**
     * Returns a [DownloadManager] to be used to downloaded content. Called only once in the
     * life cycle of the process.
     */
    override fun getDownloadManager(): DownloadManager {
         val downloadManager = get<DownloadManager>()
        downloadManager.maxParallelDownloads = 3
        downloadManager.requirements = Requirements(
            Requirements.NETWORK_UNMETERED
        )
        downloadManager.addListener(
            TerminalStateNotificationHelper(
                this,
                FOREGROUND_NOTIFICATION_ID + 1,
                get()
            )
        )
        return downloadManager
    }

    /**
     * Returns a notification to be displayed when this service running in the foreground. This method
     * is called when there is a download state change and periodically while there are active
     * downloads. The periodic update interval can be set using [.DownloadService].
     *
     *
     * On API level 26 and above, this method may also be called just before the service stops,
     * with an empty `downloads` array. The returned notification is used to satisfy system
     * requirements for foreground services.
     *
     *
     * Download services that do not wish to run in the foreground should be created by setting the
     * `foregroundNotificationId` constructor argument to [ ][.FOREGROUND_NOTIFICATION_ID_NONE]. This method will not be called in this case, meaning it can
     * be implemented to throw [UnsupportedOperationException].
     *
     * @param downloads The current downloads.
     * @return The foreground notification to display.
     */
    override fun getForegroundNotification(downloads: MutableList<Download>?): Notification {
        return get<DownloadNotificationHelper>().buildProgressNotification(
            android.R.drawable.stat_sys_download,
            null,
            null,
            downloads
        )
    }

    /**
     * Returns a [Scheduler] to restart the service when requirements allowing downloads to take
     * place are met. If `null`, the service will only be restarted if the process is still in
     * memory when the requirements are met.
     */
    override fun getScheduler(): Scheduler? {
        return WorkManagerScheduler(DOWNLOAD_WORK_MANAGER_ID)
    }


    /**
     * Creates and displays notifications for downloads when they complete or fail.
     *
     * This helper will outlive the lifespan of a single instance of [MediaDownloadService].
     * It is static to avoid leaking the first [MediaDownloadService] instance.
     */
    private class TerminalStateNotificationHelper(
        private val context: Context,
        private val nextNotificationId: Int,
        private val notificationHelper: DownloadNotificationHelper
    ) : DownloadManager.Listener {
        /**
         * Called when the state of a download changes.
         *
         * @param downloadManager The reporting instance.
         * @param download The state of the download.
         */
        override fun onDownloadChanged(downloadManager: DownloadManager?, download: Download?) {
            val notification: Notification = when (download?.state) {
                Download.STATE_COMPLETED -> {
                    notificationHelper.buildDownloadCompletedNotification(
                        android.R.drawable.stat_sys_download_done,
                        null,
                        Util.fromUtf8Bytes(download.request.data)
                    )
                }
                Download.STATE_FAILED -> {
                    notificationHelper.buildDownloadFailedNotification(
                        android.R.drawable.stat_sys_download_done,
                        null,
                        Util.fromUtf8Bytes(download.request.data)
                    )
                }
                else -> {
                    return
                }
            }
            NotificationUtil.setNotification(
                context,
                nextNotificationId+1,
                notification
            )
        }
    }

    companion object {
        const val DOWNLOAD_WORK_MANAGER_ID = "media_download_scheduler"
        const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "Downloads"
        const val FOREGROUND_NOTIFICATION_ID = 1
    }
}