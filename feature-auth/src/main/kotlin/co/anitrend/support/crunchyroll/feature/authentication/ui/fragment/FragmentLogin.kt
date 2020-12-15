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
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.extension.ext.argument
import co.anitrend.arch.ui.view.widget.model.StateLayoutConfig
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.ui.fragment.CrunchyFragment
import co.anitrend.support.crunchyroll.feature.authentication.databinding.FragmentLoginBinding
import co.anitrend.support.crunchyroll.feature.authentication.presenter.AuthPresenter
import co.anitrend.support.crunchyroll.feature.authentication.ui.activity.AuthenticationScreen
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login.LoginViewModel
import co.anitrend.support.crunchyroll.navigation.Authentication
import co.anitrend.support.crunchyroll.navigation.Main
import kotlinx.android.synthetic.main.login_anonymous_controls.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentLogin(
    private val presenter: AuthPresenter,
    private val accountManager: AccountManager,
    private val stateLayoutConfig: StateLayoutConfig
) : CrunchyFragment() {

    private lateinit var binding: FragmentLoginBinding

    private val isNewAccount by argument(
        Authentication.ARG_IS_ADDING_NEW_ACCOUNT,
        false
    )

    private val viewModel by viewModel<LoginViewModel>()

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
                    requireNotNull(isNewAccount),
                    binding.txtInputPassword.text.toString()
                )
                onUpdateUserInterface(activity?.intent)
            }
        }
        viewModelState().networkState.observe(viewLifecycleOwner) {
            binding.supportStateLayout.networkMutableStateFlow.value = it
        }
        viewModelState().refreshState.observe(viewLifecycleOwner) {
            binding.supportStateLayout.networkMutableStateFlow.value = it
        }
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state

    override fun initializeComponents(savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenResumed {
            binding.supportStateLayout.interactionStateFlow
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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.loginButton.setOnClickListener {
            onFetchDataInitialize()
        }
        binding.loginAnonymousControls.skipLoginButton.setOnClickListener {
            lifecycleScope.launch {
                presenter.onAnonymousRequest(accountManager).collect {
                    binding.supportStateLayout.networkMutableStateFlow.value = it
                    if (it == NetworkState.Success) {
                        Main(context)
                        activity?.closeScreen()
                    }
                }
            }
        }
        binding.supportStateLayout.stateConfigFlow.value = stateLayoutConfig
        binding.supportStateLayout.networkMutableStateFlow.value = NetworkState.Success

        binding.txtInputEmail.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty())
                viewModel.loginQuery.account = text.toString()
        }
        binding.txtInputPassword.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty())
                viewModel.loginQuery.password = text.toString()
        }
    }

    private fun onUpdateUserInterface(intent: Intent?) {
        val screen = activity as AuthenticationScreen?
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
            binding
        ) { viewModel.state(it) }
    }

    override fun hasBackPressableAction(): Boolean {
        if (binding.supportStateLayout.isError) {
            binding.supportStateLayout.networkMutableStateFlow.value = NetworkState.Success
            return true
        }
        return super.hasBackPressableAction()
    }
}