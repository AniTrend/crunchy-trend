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

package co.anitrend.support.crunchyroll.shared.image.viewer.ui

import android.Manifest
import android.app.DownloadManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import co.anitrend.arch.extension.ext.extra
import co.anitrend.arch.extension.ext.systemServiceOf
import co.anitrend.support.crunchyroll.android.extensions.using
import co.anitrend.support.crunchyroll.core.common.DEFAULT_ANIMATION_DURATION
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.navigation.ImageViewer
import co.anitrend.support.crunchyroll.shared.image.viewer.databinding.ImageViewScreenBinding
import coil.request.Disposable
import coil.target.Target
import com.davemorrissey.labs.subscaleview.ImageSource

class ImageViewerScreen : CrunchyActivity<ImageViewScreenBinding>() {

    private val payload: ImageViewer.Payload?
            by extra(ImageViewer.extraKey)

    private var disposable: Disposable? = null


    private val permissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isAllowed: Boolean ->
        if (isAllowed)
            downloadImage()
        else {
            TODO("Show material dialog for permission requirement")
        }
    }


    private fun setUpImagePreview() {
        disposable = object : Target {
            /**
             * Called when the request starts.
             */
            override fun onStart(placeholder: Drawable?) {

            }

            /**
             * Called if an error occurs while executing the request.
             */
            override fun onError(error: Drawable?) {

            }

            /**
             * Called if the request completes successfully.
             */
            override fun onSuccess(result: Drawable) {
                val source = ImageSource.bitmap(result.toBitmap())
                requireBinding().subSamplingImageView.setImage(source)
            }
        }.using(payload?.imageSrc, this)
    }


    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {
        super.configureActivity()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(
                WindowInsets.Type.statusBars() and WindowInsets.Type.navigationBars()
            )
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImageViewScreenBinding.inflate(layoutInflater)
        setContentView(requireBinding().root)
        setUpImagePreview()
    }

    /**
     * Additional initialization to be done in this method, this is called in during
     * [androidx.fragment.app.FragmentActivity.onPostCreate]
     *
     * @param savedInstanceState
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {
        requireBinding().subSamplingImageView.setOnClickListener {
            val transparency = requireBinding().subSamplingDownloadAction.alpha
            requireBinding().subSamplingDownloadAction.animate()
                .alpha(if (transparency == VISIBLE) HIDDEN else VISIBLE)
                .setDuration(DEFAULT_ANIMATION_DURATION).apply {
                    interpolator = DecelerateInterpolator()
                }
        }
        requireBinding().subSamplingDownloadAction.setOnClickListener {
            val transparency = requireBinding().subSamplingDownloadAction.alpha
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (transparency == VISIBLE)
                permissionResult.launch(permission)
        }
    }

    private fun downloadImage() {
        val imageUri = Uri.parse(payload?.imageSrc)
        val request = DownloadManager.Request(imageUri)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            imageUri.lastPathSegment
        )
        request.setNotificationVisibility(
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )
        val downloadManager = systemServiceOf<DownloadManager>()
        downloadManager?.enqueue(request)
    }

    companion object {
        const val VISIBLE = 1f
        const val HIDDEN = 0f
    }
}