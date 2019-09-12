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

package co.anitrend.support.crunchyroll.stream.activity

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.ui.contract.CrunchyActivity
import co.anitrend.support.crunchyroll.stream.fragment.FragmentMediaStream
import org.koin.android.ext.android.inject

class StreamingActivity : CrunchyActivity<Nothing, CrunchyCorePresenter>() {

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<CrunchyCorePresenter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming)
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.
     */
    override fun onResume() {
        super.onResume()
        onUpdateUserInterface()
    }

    /**
     * Additional initialization to be done in this method, if the overriding class is type of
     * [androidx.fragment.app.Fragment] then this method will be called in
     * [androidx.fragment.app.FragmentActivity.onCreate]. Otherwise
     * [androidx.fragment.app.FragmentActivity.onPostCreate] invokes this function
     *
     * @param savedInstanceState
     */
    override fun initializeComponents(savedInstanceState: Bundle?) {

    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [co.anitrend.arch.ui.view.contract.ISupportFragmentActivity]
     * will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        if (supportFragmentActivity == null) {
            supportFragmentActivity = FragmentMediaStream.newInstance(intent.extras).apply {
                supportFragmentManager.commit {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    replace(R.id.contentFrame, this@apply, tag)
                }
            }
        }
    }

    /**
     * Handles the complex logic required to dispatch network request to [co.anitrend.arch.core.viewmodel.SupportPagingViewModel]
     * which uses [co.anitrend.arch.data.repository.SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [co.anitrend.arch.core.viewmodel.SupportPagingViewModel.model]
     *
     * @see [co.anitrend.arch.core.viewmodel.SupportPagingViewModel.requestBundleLiveData]
     */
    override fun onFetchDataInitialize() {

    }
}