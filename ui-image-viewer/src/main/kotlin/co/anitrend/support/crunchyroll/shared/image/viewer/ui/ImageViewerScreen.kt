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
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.extension.ext.extra
import co.anitrend.arch.extension.ext.systemServiceOf
import co.anitrend.support.crunchyroll.core.common.DEFAULT_ANIMATION_DURATION
import co.anitrend.support.crunchyroll.navigation.*
import co.anitrend.support.crunchyroll.core.ui.activity.CrunchyActivity
import co.anitrend.support.crunchyroll.shared.image.viewer.databinding.ImageViewScreenBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.File

class ImageViewerScreen : CrunchyActivity() {

    private val payload: ImageViewer.Payload?
            by extra(ImageViewer.extraKey)

    private val binding by lazy(UNSAFE) {
        ImageViewScreenBinding.inflate(layoutInflater)
    }

    private val subSamplingTarget by lazy {
        object : CustomViewTarget<SubsamplingScaleImageView, File>(binding.subSamplingImageView) {
            override fun onLoadFailed(errorDrawable: Drawable?) {

            }

            override fun onResourceCleared(placeholder: Drawable?) {

            }

            override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                view.setImage(ImageSource.uri(Uri.fromFile(resource)))
            }
        }
    }

    private fun loadImage(context: Context) {
        Glide.with(context)
            .download(GlideUrl(payload?.imageSrc))
            .into(subSamplingTarget)
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
        setContentView(binding.root)
        loadImage(context = this)
    }

    /**
     * Additional initialization to be done in this method, this is called in during
     * [androidx.fragment.app.FragmentActivity.onPostCreate]
     *
     * @param savedInstanceState
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {
        binding.subSamplingImageView.setOnClickListener {
            val transparency = binding.subSamplingDownloadAction.alpha
            binding.subSamplingDownloadAction.animate()
                .alpha(if (transparency == VISIBLE) HIDDEN else VISIBLE)
                .setDuration(DEFAULT_ANIMATION_DURATION).apply {
                    interpolator = DecelerateInterpolator()
                }
        }
        binding.subSamplingDownloadAction.setOnClickListener {
            val transparency = binding.subSamplingDownloadAction.alpha
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (transparency == VISIBLE && requestPermissionIfMissing(permission))
                downloadImage()
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