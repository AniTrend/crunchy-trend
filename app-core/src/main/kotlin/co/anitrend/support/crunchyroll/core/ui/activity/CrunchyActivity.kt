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

package co.anitrend.support.crunchyroll.core.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import co.anitrend.arch.core.presenter.SupportPresenter
import co.anitrend.arch.extension.LAZY_MODE_UNSAFE
import co.anitrend.arch.ui.activity.SupportActivity
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.core.android.widgets.ElasticDragDismissFrameLayout
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.extensions.createDialog
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.core.util.config.ConfigurationUtil
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import org.koin.android.ext.android.inject

abstract class CrunchyActivity<M, P : SupportPresenter<CrunchySettings>> : SupportActivity<M, P>() {

    private val systemChromeFade by lazy(LAZY_MODE_UNSAFE) {
        object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                closeScreen()
            }
        }
    }

    protected abstract val elasticLayout: ElasticDragDismissFrameLayout?

    protected val configurationUtil by inject<ConfigurationUtil>()

    /**
     * Must be called on [onResume]
     */
    fun attachSystemChromeFade() {
        elasticLayout?.addListener(systemChromeFade)
    }

    /**
     * Must be called in [onPause]
     */
    fun detachSystemChromeFade() {
        elasticLayout?.removeListener(systemChromeFade)
    }

    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {
        configurationUtil.onCreate(this)
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */
    override fun onResume() {
        super.onResume()
        configurationUtil.onResume(this)
        attachSystemChromeFade()
    }

    /**
     * Dispatch onPause() to fragments.
     */
    override fun onPause() {
        super.onPause()
        detachSystemChromeFade()
    }

    /**
     * Handles the complex logic required to dispatch network request to [co.anitrend.arch.core.viewmodel.contract.ISupportViewModel]
     * to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [co.anitrend.arch.core.viewmodel.contract.ISupportViewModel.model]
     *
     * @see [co.anitrend.arch.core.viewmodel.contract.ISupportViewModel.invoke]
     */
    override fun onFetchDataInitialize() {
        // may not be used in most activities so we're making it optional
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *
     *
     * @param requestCode The request code passed in [.requestPermissions].
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [android.content.pm.PackageManager.PERMISSION_GRANTED]
     * or [android.content.pm.PackageManager.PERMISSION_DENIED]. Never null.
     *
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == compatViewPermissionValue) {
            val denied = grantResults.filter {
                it != PackageManager.PERMISSION_GRANTED
            }
            if (denied.isNotEmpty())
                checkStoragePermission()

        }
    }

    protected fun checkStoragePermission() {
        if (!requestPermissionIfMissing(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) createDialog(BottomSheet(LayoutMode.WRAP_CONTENT))
                ?.title(
                    res = R.string.dialog_title_permission_reason_text
                )?.message(
                    res = R.string.dialog_message_permission_reason_text
                )?.positiveButton(
                    res = R.string.dialog_button_text_got_it,
                    click = {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            compatViewPermissionValue
                        )
                    }
                )?.show()
            else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    compatViewPermissionValue
                )
            }
        }
    }
}