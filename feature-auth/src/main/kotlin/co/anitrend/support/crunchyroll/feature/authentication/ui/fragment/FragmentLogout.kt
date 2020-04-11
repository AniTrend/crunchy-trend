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
import co.anitrend.support.crunchyroll.feature.authentication.presenter.AuthPresenter
import co.anitrend.support.crunchyroll.core.presenter.CrunchyCorePresenter
import co.anitrend.support.crunchyroll.feature.authentication.databinding.FragmentLogoutBinding
import co.anitrend.support.crunchyroll.feature.authentication.viewmodel.LogoutViewModel
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class FragmentLogout : SupportFragment<Boolean, CrunchyCorePresenter, Boolean>() {

    private lateinit var binding: FragmentLogoutBinding

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return supportPresenter of the generic type specified
     */
    override val supportPresenter by inject<AuthPresenter>()

    /**
     * Should be created lazily through injection or lazy delegate
     *
     * @return view model of the given type
     */
    override val supportViewModel by inject<LogoutViewModel>()

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
        binding.supportStateLayout.stateConfiguration = get()
        binding.supportStateLayout.onWidgetInteraction = View.OnClickListener {
            supportViewModel.retry()
        }
        binding.supportStateLayout.setNetworkState(NetworkState.Success)
        binding.userLogoutButton.setOnClickListener {
            onFetchDataInitialize()
        }
        setUpViewModelObserver()
    }

    /**
     * Invoke view model observer to watch for changes
     */
    override fun setUpViewModelObserver() {
        supportViewModel.model.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it == true) {
                    NavigationTargets.Splash(context)
                    activity?.closeScreen()
                }
            }
        })
        supportViewModel.networkState?.observe(viewLifecycleOwner, Observer {
            binding.supportStateLayout.setNetworkState(it)
        })
        supportViewModel.refreshState?.observe(viewLifecycleOwner, Observer {
            binding.supportStateLayout.setNetworkState(it)
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

    override fun onResume() {
        super.onResume()
        onUpdateUserInterface()
    }

    /**
     * Handles the updating of views, binding, creation or state change, depending on the context
     * [androidx.lifecycle.LiveData] for a given [ISupportFragmentActivity] will be available by this point.
     *
     * Check implementation for more details
     */
    override fun onUpdateUserInterface() {
        val liveData = supportViewModel.getCurrentUser()
        liveData.observe(viewLifecycleOwner, Observer {
            binding.currentUserModel = it
        })
    }

    /**
     * Handles the complex logic required to dispatch network request to [SupportPagingViewModel]
     * to either request from the network or database cache.
     *
     * The results of the dispatched network or cache call will be published by the
     * [androidx.lifecycle.LiveData] specifically [SupportPagingViewModel.model]
     *
     * @see [SupportPagingViewModel.requestBundleLiveData]
     */
    override fun onFetchDataInitialize() {
        supportViewModel(null)
    }

    override fun hasBackPressableAction(): Boolean {
        if (binding.supportStateLayout.isError) {
            binding.supportStateLayout.setNetworkState(NetworkState.Success)
            return true
        }
        return super.hasBackPressableAction()
    }


    companion object {
        fun newInstance(): FragmentLogout {
            return FragmentLogout()
        }
    }
}