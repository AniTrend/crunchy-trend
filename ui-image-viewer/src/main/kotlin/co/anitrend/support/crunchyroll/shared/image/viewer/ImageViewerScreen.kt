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

package co.anitrend.support.crunchyroll.shared.image.viewer

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import co.anitrend.arch.extension.ext.LAZY_MODE_UNSAFE
import co.anitrend.arch.extension.ext.extra
import co.anitrend.arch.extension.ext.systemServiceOf
import co.anitrend.support.crunchyroll.core.common.DEFAULT_ANIMATION_DURATION
import co.anitrend.support.crunchyroll.navigation.NavigationTargets
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

    private val payload: NavigationTargets.ImageViewer.Payload?
            by extra(NavigationTargets.ImageViewer.PAYLOAD)

    private val binding by lazy(LAZY_MODE_UNSAFE) {
        ImageViewScreenBinding.inflate(layoutInflater)
    }

    private val subSamplingTarget
            by lazy(LAZY_MODE_UNSAFE) {
                object : CustomViewTarget<SubsamplingScaleImageView, File>(
                    binding.subSamplingImageView
                ) {
                    /**
                     * A **mandatory** lifecycle callback that is called when a load fails.
                     *
                     *
                     * Note - This may be called before [.onLoadStarted]
                     * if the model object is null.
                     *
                     *
                     * You **must** ensure that any current Drawable received in [.onResourceReady] is no longer used before redrawing the container (usually a View) or changing its
                     * visibility.
                     *
                     * @param errorDrawable The error drawable to optionally show, or null.
                     */
                    override fun onLoadFailed(errorDrawable: Drawable?) {

                    }

                    /**
                     * A required callback invoked when the resource is no longer valid and must be freed.
                     *
                     *
                     * You must ensure that any current Drawable received in [.onResourceReady] is no longer used before redrawing the container (usually a View) or changing its
                     * visibility. **Not doing so will result in crashes in your app.**
                     *
                     * @param placeholder The placeholder drawable to optionally show, or null.
                     */
                    override fun onResourceCleared(placeholder: Drawable?) {

                    }

                    /**
                     * The method that will be called when the resource load has finished.
                     *
                     * @param resource the loaded resource.
                     */
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
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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
        val downloadManager =
            systemServiceOf<DownloadManager>(Context.DOWNLOAD_SERVICE)
        downloadManager?.enqueue(request)
    }

    companion object {
        const val VISIBLE = 1f
        const val HIDDEN = 0f
    }
}