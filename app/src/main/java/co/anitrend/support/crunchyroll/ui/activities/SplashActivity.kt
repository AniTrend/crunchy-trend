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

package co.anitrend.support.crunchyroll.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.work.*
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.worker.CoreSessionWorker
import io.wax911.support.ui.activity.SupportActivity
import org.koin.android.ext.android.inject
import timber.log.Timber

class SplashActivity : SupportActivity<Nothing, CrunchyCorePresenter>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        makeRequest()
    }

    /**
     * Can be used to configure custom theme styling as desired
     */
    override fun configureActivity() {

    }

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<CrunchyCorePresenter>()

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
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun updateUI() {
        val intent = when (supportPresenter.supportPreference.isAuthenticated) {
            true -> Intent(this, MainActivity::class.java)
            else -> Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

    /**
     * Handles the complex logic required to dispatch network request to [SupportViewModel]
     * which uses [SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportViewModel.model]
     *
     * @see [SupportViewModel.queryFor]
     */
    override fun makeRequest() {
        val coreSessionRequest = OneTimeWorkRequest.Builder(CoreSessionWorker::class.java)
            .addTag(CoreSessionWorker.TAG)
            .build()

        WorkManager.getInstance(this).apply {
            enqueue(coreSessionRequest)
        }.also {  workManager ->
            workManager.getWorkInfoByIdLiveData(coreSessionRequest.id)
                .observe(this, Observer {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> updateUI()
                        WorkInfo.State.FAILED -> {
                            Toast.makeText(applicationContext, "Failed to start session, retrying!", Toast.LENGTH_SHORT).show()
                            makeRequest()
                        }
                        else -> Timber.tag(moduleTag).d("${it.state}")
                    }
                })
        }
    }
}