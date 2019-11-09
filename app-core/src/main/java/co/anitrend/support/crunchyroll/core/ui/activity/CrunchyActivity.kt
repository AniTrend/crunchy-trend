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

import co.anitrend.arch.core.presenter.SupportPresenter
import co.anitrend.arch.ui.activity.SupportActivity
import co.anitrend.support.crunchyroll.core.settings.CrunchySettings
import co.anitrend.support.crunchyroll.core.util.config.ConfigurationUtil
import org.koin.android.ext.android.inject

abstract class CrunchyActivity<M, P : SupportPresenter<CrunchySettings>> : SupportActivity<M, P>() {

    protected val configurationUtil by inject<ConfigurationUtil>()

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
}