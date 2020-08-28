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
import android.os.Bundle
import android.view.Window
import androidx.core.app.ActivityCompat
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.ui.activity.SupportActivity
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.core.extensions.createDialog
import co.anitrend.support.crunchyroll.core.util.config.ConfigurationUtil
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

abstract class CrunchyActivity : SupportActivity(), KoinScopeComponent {

    protected val configurationUtil by inject<ConfigurationUtil>()

    private val scopeID: ScopeID by lazy(UNSAFE) { getScopeId() }
    override val koin by lazy(UNSAFE) { getKoin() }
    override val scope by lazy(UNSAFE) {
        koin.createScope(scopeID, getScopeName(), this)
    }

    /**
     * inject lazily
     * @param qualifier - bean qualifier / optional
     * @param scope
     * @param parameters - injection parameters
     */
    inline fun <reified T : Any> inject(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
    ) = lazy(LazyThreadSafetyMode.NONE) { get<T>(qualifier, parameters) }

    /**
     * get given dependency
     * @param name - bean name
     * @param scope
     * @param parameters - injection parameters
     */
    inline fun <reified T : Any> get(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
    ): T = runCatching {
        scope.get<T>(qualifier, parameters)
    }.getOrElse {
        koin.get(qualifier, parameters)
    }

    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        configurationUtil.onCreate(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching {
            getKoin()._logger.debug("Open activity scope: $scope")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        runCatching {
            getKoin()._logger.debug("Close activity scope: $scope")
            scope.close()
        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */
    override fun onResume() {
        super.onResume()
        configurationUtil.onResume(this)
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [requestPermissions].
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *
     * @param requestCode The request code passed in [requestPermissions].
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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