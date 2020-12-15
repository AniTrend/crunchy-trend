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

package co.anitrend.support.crunchyroll.feature.player.plugin

import android.content.Context
import android.net.Uri
import co.anitrend.support.crunchyroll.core.extensions.moduleTag
import co.anitrend.support.crunchyroll.feature.player.service.MediaDownloadService
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.offline.DownloadHelper.getDefaultTrackSelectorParameters
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

class TrackerPlugin(
    private val context: Context,
    downloadManager: DownloadManager
) {

    private val listeners = CopyOnWriteArraySet<Listener>()
    private val downloads = HashMap<Uri, Download>()
    private val downloadIndex = downloadManager.downloadIndex
    private val trackSelectorParameters = getDefaultTrackSelectorParameters(context)

    init {
        downloadManager.addListener(
            DownloadManagerListener()
        )
        loadDownloads()
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun isDownloaded(uri: Uri?): Boolean {
        val download = downloads[uri]
        return download != null && download.state != Download.STATE_FAILED
    }

    fun getDownloadRequest(uri: Uri?): DownloadRequest? {
        val download = downloads[uri]
        return if (download != null && download.state != Download.STATE_FAILED) download.request else null
    }

    fun toggleDownload(
        uri: Uri?
    ) {
        val download = downloads[uri]
        if (download != null) {
            DownloadService.sendRemoveDownload(
                context,
                MediaDownloadService::class.java,
                download.request.id,
                false
            )
        }
    }

    private fun loadDownloads() {
        try {
            downloadIndex.getDownloads().use { loadedDownloads ->
                while (loadedDownloads.moveToNext()) {
                    val download = loadedDownloads.download
                    downloads[download.request.uri] = download
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private inner class DownloadManagerListener :
        DownloadManager.Listener {
        override fun onDownloadChanged(
            downloadManager: DownloadManager,
            download: Download,
            finalException: Exception?
        ) {
            downloads[download.request.uri] = download
            if (finalException != null)
                Timber.tag(moduleTag).e(finalException)
            listeners.forEach { it.onDownloadsChanged() }
        }

        override fun onDownloadRemoved(
            downloadManager: DownloadManager,
            download: Download
        ) {
            downloads.remove(download.request.uri)
            for (listener in listeners) {
                listener.onDownloadsChanged()
            }
        }
    }


    /** Listens for changes in the tracked downloads.  */
    interface Listener {
        /** Called when the tracked downloads changed.  */
        fun onDownloadsChanged()
    }
}