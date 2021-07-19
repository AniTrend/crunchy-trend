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

package co.anitrend.support.crunchyroll.feature.authentication.ui.fragment

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.domain.entities.LoadState
import co.anitrend.arch.extension.ext.argument
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.android.binding.IBindingView
import co.anitrend.support.crunchyroll.feature.authentication.databinding.FragmentLoginBinding
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.ui.fragment.CrunchyFragment
import co.anitrend.support.crunchyroll.feature.authentication.presenter.AuthPresenter
import co.anitrend.support.crunchyroll.feature.authentication.ui.activity.AuthenticationScreen
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login.LoginViewModel
import co.anitrend.support.crunchyroll.navigation.Authentication
import co.anitrend.support.crunchyroll.navigation.Main
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentLogin(
    private val presenter: AuthPresenter,
    private val accountManager: AccountManager,
    private val stateLayoutConfig: StateLayoutConfig
) : CrunchyFragment(), IBindingView<FragmentLoginBinding> {

    override var binding: FragmentLoginBinding? = null

    private val isNewAccount by argument(
        Authentication.ARG_IS_ADDING_NEW_ACCOUNT,
        false
    )

    private val viewModel by viewModel<LoginViewModel>()

    private val restNetworkStateOnBackPress =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (requireBinding().supportStateLayout.isError)
                    requireBinding().supportStateLayout.loadStateFlow.value = LoadState.Success()
                else
                    activity?.closeScreen()
            }
        }

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(viewLifecycleOwner) { user ->
            // view model live data will initially return a null
            if (user != null) {
                presenter.onLoginStateChange(
                    user,
                    accountManager,
                    isNewAccount,
                    requireBinding().txtInputPassword.text.toString()
                )
                onUpdateUserInterface(activity?.intent)
            }
        }
        viewModelState().loadState.observe(viewLifecycleOwner) {
            requireBinding().supportStateLayout.loadStateFlow.value = it
        }
        viewModelState().refreshState.observe(viewLifecycleOwner) {
            requireBinding().supportStateLayout.loadStateFlow.value = it
        }
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state

    override fun initializeComponents(savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
            this, restNetworkStateOnBackPress
        )
        lifecycleScope.launchWhenResumed {
            requireBinding().supportStateLayout.interactionFlow
                .filterNotNull()
                .debounce(DEBOUNCE_DURATION)
                .collect {
                    viewModelState().retry()
                }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentLoginBinding.inflate(inflater, container, false).let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireBinding().lifecycleOwner = viewLifecycleOwner
        requireBinding().loginButton.setOnClickListener {
            onFetchDataInitialize()
        }
        requireBinding().loginAnonymousControls.skipLoginButton.setOnClickListener {
            lifecycleScope.launch {
                presenter.onAnonymousRequest(accountManager).collect {
                    requireBinding().supportStateLayout.loadStateFlow.value = it
                    if (it is LoadState.Success) {
                        Main(context)
                        activity?.closeScreen()
                    }
                }
            }
        }
        requireBinding().supportStateLayout.stateConfigFlow.value = stateLayoutConfig
        requireBinding().supportStateLayout.loadStateFlow.value = LoadState.Success()

        requireBinding().txtInputEmail.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty())
                viewModel.loginQuery.account = text.toString()
        }
        requireBinding().txtInputPassword.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty())
                viewModel.loginQuery.password = text.toString()
        }
    }

    private fun onUpdateUserInterface(intent: Intent?) {
        val screen = activity as? AuthenticationScreen
        if (intent == null) {
            screen?.setResult(Activity.RESULT_CANCELED)
        } else {
            screen?.setAccountAuthenticatorResult(requireNotNull(intent.extras))
            screen?.setResult(Activity.RESULT_OK, intent)
            Main(context)
        }
        activity?.closeScreen()
    }

    private fun onFetchDataInitialize() {
        presenter.onSubmit(
            viewModel.loginQuery,
            requireBinding()
        ) { viewModel.state(it) }
    }
}