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
import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.domain.entities.NetworkState
import co.anitrend.arch.ui.fragment.SupportFragment
import co.anitrend.support.crunchyroll.core.extensions.closeScreen
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.core.ui.fragment.IFragmentFactory
import co.anitrend.support.crunchyroll.domain.user.entities.CrunchyUser
import co.anitrend.support.crunchyroll.feature.authentication.databinding.FragmentLoginBinding
import co.anitrend.support.crunchyroll.feature.authentication.presenter.AuthPresenter
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.login.LoginViewModel
import kotlinx.android.synthetic.main.login_anonymous_controls.view.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class FragmentLogin : SupportFragment<CrunchyUser?>() {

    private lateinit var binding: FragmentLoginBinding

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    private val presenter by inject<AuthPresenter>()

    private val viewModel by inject<LoginViewModel>()

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        viewModelState().model.observe(viewLifecycleOwner, Observer {
            if (it != null)
                onUpdateUserInterface()
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentLoginBinding.inflate(inflater, container, false).let {
            binding = it
            it.root
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

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        presenter.onLoginStateChange(viewModelState().model.value)
        NavigationTargets.Main(context)
        activity?.closeScreen()
    }

    /**
     * Handles the complex logic required to dispatch network request to [SupportViewModel]
     * which uses [SupportRepository] to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [LoginViewModel.model]
     *
     * @see [LoginViewModel.invoke]
     */
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

    companion object : IFragmentFactory<FragmentLogin> {
        override val FRAGMENT_TAG = FragmentLogin::class.java.simpleName

        override fun newInstance(bundle: Bundle?) = FragmentLogin()
    }
}