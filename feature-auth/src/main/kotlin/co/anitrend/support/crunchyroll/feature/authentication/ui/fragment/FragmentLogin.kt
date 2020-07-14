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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.support.crunchyroll.core.common.DEBOUNCE_DURATION
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.navigation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.CrunchyFragment
import co.anitrend.support.crunchyroll.feature.authentication.databinding.FragmentLoginBinding
import co.anitrend.support.crunchyroll.feature.authentication.presenter.AuthPresenter
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login.LoginViewModel
import kotlinx.android.synthetic.main.login_anonymous_controls.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentLogin(
    private val presenter: AuthPresenter
) : CrunchyFragment() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel by viewModel<LoginViewModel>()

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(
            viewLifecycleOwner,
            Observer {
                if (it != null) {
                    presenter.onLoginStateChange(it)
                    onUpdateUserInterface()
                }
            }
        )
        viewModelState().networkState.observe(
            viewLifecycleOwner,
            Observer {
                binding.supportStateLayout.networkMutableStateFlow.value = it
            }
        )
        viewModelState().refreshState.observe(
            viewLifecycleOwner,
            Observer {
                binding.supportStateLayout.networkMutableStateFlow.value = it
            }
        )
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
            NavigationTargets.Main(context)
            activity?.closeScreen()
        }
        binding.supportStateLayout.stateConfigFlow.value = get()
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

    private fun onUpdateUserInterface() {
        NavigationTargets.Main(context)
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

    /**
     * Expects a module helper if one is available for the current scope, otherwise return null
     */
    override fun featureModuleHelper(): Nothing? = null
}