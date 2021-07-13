/*
 *    Copyright 2021 AniTrend
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

package co.anitrend.support.crunchyroll.core.ui.fragment.list.presenter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import co.anitrend.arch.ui.fragment.list.contract.ISupportFragmentList
import co.anitrend.arch.ui.fragment.list.presenter.SupportListPresenter
import co.anitrend.arch.ui.view.widget.contract.ISupportStateLayout
import co.anitrend.support.crunchyroll.core.android.binding.IBindingView
import co.anitrend.support.crunchyroll.core.databinding.SharedListContentBinding

class CrunchyFragmentListPresenter<M> : SupportListPresenter<M>(),
    IBindingView<SharedListContentBinding> {

    override val recyclerView: RecyclerView
        get() = requireBinding().recyclerView

    override val stateLayout: ISupportStateLayout
        get() = requireBinding().stateLayout

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = requireBinding().swipeRefreshLayout

    override var binding: SharedListContentBinding? = null

    override fun onCreateView(fragmentList: ISupportFragmentList<M>, view: View?) {
        binding = SharedListContentBinding.bind(requireNotNull(view))
        super.onCreateView(fragmentList, view)
    }
}