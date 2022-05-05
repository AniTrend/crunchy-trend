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

import android.os.Bundle
import android.view.Window
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import co.anitrend.arch.core.analytic.contract.ISupportAnalytics
import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.ui.activity.SupportActivity
import co.anitrend.support.crunchyroll.android.binding.IBindingView
import co.anitrend.support.crunchyroll.core.util.config.ConfigurationUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.setupKoinFragmentFactory
import org.koin.androidx.scope.activityScope
import org.koin.core.component.KoinScopeComponent
import timber.log.Timber

abstract class CrunchyActivity<B : ViewBinding> : SupportActivity(), KoinScopeComponent,
    IBindingView<B> {

    protected val configurationUtil by inject<ConfigurationUtil>()

    protected val supportAnalytics by inject<ISupportAnalytics>()

    override val scope by activityScope()

    override var binding: B? = null

    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        configurationUtil.onCreate(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            runCatching {
                Timber.d("Open activity scope: $scope")
                setupKoinFragmentFactory(scope)
            }.onFailure {
                setupKoinFragmentFactory()
                Timber.w(it, "Defaulting to scope-less based fragment factory")
            }
            supportAnalytics.logCurrentState(javaClass.simpleName, intent.extras)
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
     * Proxy for a view model state if one exists
     */
    override fun viewModelState(): ISupportViewModelState<*>? = null

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}