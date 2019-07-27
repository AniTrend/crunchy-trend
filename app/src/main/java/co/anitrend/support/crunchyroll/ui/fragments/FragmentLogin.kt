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

package co.anitrend.support.crunchyroll.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.ViewSwitcher
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import co.anitrend.support.crunchyroll.R
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.core.viewmodel.auth.CrunchyAuthViewModel
import co.anitrend.support.crunchyroll.data.auth.model.CrunchyLogin
import co.anitrend.support.crunchyroll.data.usecase.auth.CrunchyAuthenticationUseCase
import co.anitrend.support.crunchyroll.data.usecase.auth.CrunchyAuthenticationUseCase.Payload.RequestType.Companion.LOG_IN
import co.anitrend.support.crunchyroll.data.usecase.auth.CrunchyAuthenticationUseCase.Payload.RequestType.Companion.LOG_OUT
import co.anitrend.support.crunchyroll.ui.activities.MainActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.button.MaterialButton
import io.wax911.support.extension.gone
import io.wax911.support.extension.visible
import io.wax911.support.ui.fragment.SupportFragment
import org.koin.android.ext.android.inject
import timber.log.Timber

class FragmentLogin : SupportFragment<CrunchyLogin?, CrunchyCorePresenter, CrunchyLogin?>() {

    private lateinit var editLoginText: EditText
    private lateinit var editPasswordText: EditText
    private lateinit var buttonLogin: MaterialButton
    private lateinit var buttonLoginSkip: MaterialButton
    private lateinit var viewSwitcherLogin: ViewSwitcher

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<CrunchyCorePresenter>()

    override val supportViewModel by inject<CrunchyAuthViewModel>()

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(this, Observer {
            if (it != null) {
                supportPresenter.onLoginSuccess(it)
                onUpdateUserInterface()
            }
            else
                Timber.tag(moduleTag).e("Unable to login")
        })
        supportViewModel.networkState?.observe(this, Observer {
            if (it.code != null) {
                when {
                    it.message == null -> onUpdateUserInterface()
                    else -> {
                        viewSwitcherLogin.showPrevious()
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
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
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null. This will be called between
     * [.onCreate] and [.onActivityCreated].
     *
     * A default View can be returned by calling [.Fragment] in your
     * constructor. Otherwise, this method returns null.
     *
     *
     * It is recommended to **only** inflate the layout in this method and move
     * logic that operates on the returned View to [.onViewCreated].
     *
     *
     * If you return a View from here, you will later be called in
     * [.onDestroyView] when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)?.apply {
            editLoginText = findViewById(R.id.txt_input_email)
            editPasswordText = findViewById(R.id.txt_input_password)
            buttonLogin = findViewById(R.id.loginButton)
            buttonLoginSkip = findViewById(R.id.skipLoginButton)
            viewSwitcherLogin = findViewById(R.id.view_switcher_login)
        }
    }

    /**
     * Called immediately after [.onCreateView]
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     * @param view The View returned by [.onCreateView].
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonLogin.setOnClickListener {
            onFetchDataInitialize()
        }
        buttonLoginSkip.setOnClickListener {
            onUpdateUserInterface()
        }
        setUpViewModelObserver()
    }

    override fun onResume() {
        super.onResume()
        if (supportPresenter.supportPreference.isAuthenticated) {
            buttonLogin.setText(R.string.action_logout)
            editLoginText.apply {
                setText(supportPresenter.supportPreference.authenticatedUserId.toString())
                isEnabled = false
            }
            editPasswordText.gone()
        } else {
            buttonLogin.setText(R.string.action_login)
            editPasswordText.visible()
        }
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        supportPresenter.startSessionWorker().also { workManager ->
            workManager.getWorkInfoByIdLiveData(supportPresenter.coreSessionRequest.id)
                .observe(this, Observer {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            startActivity(Intent(activity, MainActivity::class.java))
                            activity?.finish()
                        }
                        WorkInfo.State.FAILED -> {
                            onSessionActivationFailed()
                        }
                        else -> Timber.tag(moduleTag).d("${it.state}")
                    }
                })
        }
    }

    private fun onSessionActivationFailed() {
        activity?.also { fragmentActivity ->
            MaterialDialog(fragmentActivity)
                .title(text = "Session not started")
                .message(text = "Application failed to start session, would you like to skip this this stage and retry it later?")
                .positiveButton(text = "Yes", click = {
                    startActivity(
                        Intent(fragmentActivity, MainActivity::class.java)
                    )
                    fragmentActivity.finish()
                })
                .negativeButton(text = "Retry", click = { onUpdateUserInterface() })
                .show()
        }
    }

    /**
     * Handles the complex logic required to dispatch network request to [SupportViewModel]
     * which uses [SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportViewModel.model]
     *
     * @see [SupportViewModel.requestBundleLiveData]
     */
    override fun onFetchDataInitialize() {
        viewSwitcherLogin.showNext()
        supportViewModel(CrunchyAuthenticationUseCase.Payload(
            account = editLoginText.editableText.toString(),
            password = editPasswordText.editableText.toString(),
            authenticationType = if (!supportPresenter.supportPreference.isAuthenticated) LOG_IN else LOG_OUT
        ))
    }

    companion object {
        fun newInstance(): FragmentLogin {
            return FragmentLogin()
        }
    }
}