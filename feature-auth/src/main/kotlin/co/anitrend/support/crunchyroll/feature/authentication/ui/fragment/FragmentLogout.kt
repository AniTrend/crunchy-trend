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
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import co.anitrend.arch.domain.entities.LoadState
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.ui.fragment.CrunchyFragment
import co.anitrend.support.crunchyroll.feature.authentication.databinding.FragmentLogoutBinding
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login.LoginViewModel
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.logout.LogoutViewModel
import co.anitrend.support.crunchyroll.navigation.*
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentLogout : CrunchyFragment() {

    private lateinit var binding: FragmentLogoutBinding

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return view model of the given type
     */
    private val viewModel by viewModel<LogoutViewModel>()
    private val viewModelUser by viewModel<LoginViewModel>()


    private val restNetworkStateOnBackPress =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.supportStateLayout.isError)
                    binding.supportStateLayout.loadStateFlow.value = LoadState.Success()
                else
                    activity?.closeScreen()
            }
        }

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModelUser.state.model.observe(viewLifecycleOwner, Observer {
            binding.currentUserModel = it
        })
        viewModelState().model.observe(viewLifecycleOwner, Observer { loggedOut ->
            if (loggedOut) {
                Splash(context)
                activity?.closeScreen()
            }
        })
        viewModelState().loadState.observe(viewLifecycleOwner, {
            binding.supportStateLayout.loadStateFlow.value = it
        })
        viewModelState().refreshState.observe(viewLifecycleOwner, {
            binding.supportStateLayout.loadStateFlow.value = it
        })
    }

    override fun initializeComponents(savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(
        this, restNetworkStateOnBackPress
        )
        /*lifecycleScope.launchWhenResumed {
            binding.supportStateLayout.interactionStateFlow
                .filterNotNull()
                .debounce(DEBOUNCE_DURATION)
                .collect {
                    viewModelState().retry()
                }
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentLogoutBinding.inflate(inflater, container, false).let {
            binding = it
            it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.supportStateLayout.stateConfigFlow.value = get()
        binding.supportStateLayout.loadStateFlow.value = LoadState.Success()
        binding.userLogoutButton.setOnClickListener {
            viewModelState().invoke()
        }
        onFetchDataInitialize()
    }

    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState() = viewModel.state

    private fun onFetchDataInitialize() {
        viewModelUser.state.invoke()
    }
}