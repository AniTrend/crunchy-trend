/*
 *    Copyright 2020 AniTrend
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

package co.anitrend.support.crunchyroll.core.ui.fragment.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.anitrend.arch.core.model.ISupportViewModelState
import co.anitrend.arch.extension.ext.UNSAFE
import co.anitrend.arch.extension.ext.getColorFromAttr
import co.anitrend.arch.recycler.SupportRecyclerView
import co.anitrend.arch.recycler.shared.adapter.SupportLoadStateAdapter
import co.anitrend.arch.ui.fragment.list.SupportFragmentList
import co.anitrend.arch.ui.fragment.list.presenter.SupportListPresenter
import co.anitrend.support.crunchyroll.core.R
import co.anitrend.support.crunchyroll.core.ui.fragment.list.presenter.CrunchyFragmentListPresenter
import org.koin.androidx.scope.fragmentScope
import org.koin.core.scope.KoinScopeComponent

abstract class CrunchyFragmentList<M>(
    override val inflateLayout: Int = R.layout.shared_list_content,
    override val listPresenter: SupportListPresenter<M> = CrunchyFragmentListPresenter()
) : SupportFragmentList<M>(), KoinScopeComponent {

    override val scope by lazy(UNSAFE) { fragmentScope() }

    /**
     * Sets the adapter for the recycler view
     */
    override fun setRecyclerAdapter(recyclerView: SupportRecyclerView) {
        if (recyclerView.adapter == null) {
            val header = SupportLoadStateAdapter(resources, stateConfig).apply {
                registerFlowListener()
            }
            val footer = SupportLoadStateAdapter(resources, stateConfig).apply {
                registerFlowListener()
            }

            if (supportViewAdapter is RecyclerView.Adapter<*>) {
                (supportViewAdapter as RecyclerView.Adapter<*>)
                    .stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            recyclerView.adapter = supportViewAdapter.withLoadStateHeaderAndFooter(
                header = header, footer = footer
            )
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation). This will be called between
     * [onCreate] and [onActivityCreated].
     *
     * If you return a View from here, you will later be called in
     * [onDestroyView] when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the [View] for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        listPresenter.swipeRefreshLayout?.setColorSchemeColors(
            requireContext().getColorFromAttr(R.attr.colorPrimary),
            requireContext().getColorFromAttr(R.attr.colorOnBackground)
        )
        return view
    }


    /**
     * Proxy for a view model state if one exists
     */
    override fun viewModelState(): ISupportViewModelState<*>? = null
}