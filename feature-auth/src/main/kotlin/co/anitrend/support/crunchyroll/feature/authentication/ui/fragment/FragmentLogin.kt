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
import androidx.lifecycle.Observer
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.feature.authentication.databinding.FragmentLoginBinding
import co.anitrend.support.crunchyroll.feature.authentication.presenter.AuthPresenter
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login.LoginViewModel
import kotlinx.android.synthetic.main.login_anonymous_controls.view.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentLogin(
    private val presenter: AuthPresenter
) : SupportFragment<Nothing>() {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel by viewModel<LoginViewModel>()

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                presenter.onLoginStateChange(it)
                onUpdateUserInterface()
            }
        })
        viewModelState().networkState.observe(viewLifecycleOwner, Observer {
            binding.supportStateLayout.setNetworkState(it)
        })
        viewModelState().refreshState.observe(viewLifecycleOwner, Observer {
            binding.supportStateLayout.setNetworkState(it)
        })
        binding.supportStateLayout.interactionLiveData.observe(
            viewLifecycleOwner,
            Observer {
                viewModelState().retry()
            }
        )
        with(binding) {
            lifecycleOwner = this@FragmentLogin
            viewModel = viewModel
        }
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state

    override fun initializeComponents(savedInstanceState: Bundle?) {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentLoginBinding.inflate(inflater, container, false).let {
            binding = it
            it.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginButton.setOnClickListener {
            onFetchDataInitialize()
        }
        binding.loginAnonymousControls.skipLoginButton.setOnClickListener {
            NavigationTargets.Main(context)
            activity?.closeScreen()
        }
        binding.supportStateLayout.stateConfig = get()
        binding.supportStateLayout.setNetworkState(NetworkState.Success)
    }

    override fun onUpdateUserInterface() {
        NavigationTargets.Main(context)
        activity?.closeScreen()
    }

    override fun onFetchDataInitialize() {
        presenter.onSubmit(
            viewModel.loginQuery,
            binding
        ) { viewModel.state(viewModel.loginQuery) }
    }

    override fun hasBackPressableAction(): Boolean {
        if (binding.supportStateLayout.isError) {
            binding.supportStateLayout.setNetworkState(NetworkState.Success)
            return true
        }
        return super.hasBackPressableAction()
    }
}