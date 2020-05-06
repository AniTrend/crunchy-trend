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

package co.anitrend.support.crunchyroll.feature.catalog.controller.items

import android.view.View
import co.anitrend.support.crunchyroll.core.android.extensions.setImageUrl
import co.anitrend.support.crunchyroll.core.naviagation.NavigationTargets
import co.anitrend.support.crunchyroll.domain.series.entities.CrunchySeries
import co.anitrend.support.crunchyroll.feature.catalog.R
import co.anitrend.support.crunchyroll.feature.catalog.databinding.AdapterCatalogItemBinding
import coil.request.RequestDisposable
import com.xwray.groupie.viewbinding.BindableItem
import com.xwray.groupie.viewbinding.GroupieViewHolder

class CatalogItem(
    private val series: CrunchySeries
) : BindableItem<AdapterCatalogItemBinding>() {

    private var disposable: RequestDisposable? = null

    override fun getLayout() = R.layout.adapter_catalog_item

    /**
     * Perform any actions required to set up the view for display.
     *
     * @param viewBinding The ViewDataBinding to bind
     * @param position The adapter position
     */
    override fun bind(viewBinding: AdapterCatalogItemBinding, position: Int) {
        disposable = viewBinding.seriesImage.setImageUrl(series.portraitImage)
        viewBinding.seriesTitle.text = series.name
        viewBinding.seriesCard.setOnClickListener {
            val seriesPayload = NavigationTargets.Series.Payload(
                seriesId = series.seriesId
            )
            NavigationTargets.Series(
                it.context, seriesPayload
            )
        }
    }

    override fun initializeViewBinding(view: View): AdapterCatalogItemBinding =
        AdapterCatalogItemBinding.bind(view)

    override fun unbind(viewHolder: GroupieViewHolder<AdapterCatalogItemBinding>) {
        viewHolder.binding.seriesCard.setOnClickListener(null)
        disposable?.dispose()
        disposable = null
        super.unbind(viewHolder)
    }
}