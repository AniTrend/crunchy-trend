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

package co.anitrend.support.crunchyroll.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.anitrend.support.crunchyroll.data.model.rss.CrunchyRssMedia
import co.anitrend.support.crunchyroll.databinding.AdapterMediaFeedBinding
import io.wax911.support.core.presenter.SupportPresenter
import io.wax911.support.ui.recycler.adapter.SupportViewAdapter
import io.wax911.support.ui.recycler.holder.SupportViewHolder
import io.wax911.support.ui.recycler.holder.event.ItemClickListener

class RssMediaAdapter(
    presenter: SupportPresenter<*>,
    private val clickListener: ItemClickListener<CrunchyRssMedia>
) : SupportViewAdapter<CrunchyRssMedia>(presenter) {

    /**
     * Should provide the required view holder, this function is a substitute for [onCreateViewHolder] which now
     * has extended functionality
     */
    override fun createDefaultViewHolder(
        parent: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): SupportViewHolder<CrunchyRssMedia> {
        return MediaRssViewHolder(AdapterMediaFeedBinding.inflate(layoutInflater, parent, false))
    }


    inner class MediaRssViewHolder(
        private val binding: AdapterMediaFeedBinding
    ): SupportViewHolder<CrunchyRssMedia>(binding.root) {

        /**
         * Load images, text, buttons, etc. in this method from the given parameter
         *
         * @param model Is the liveData at the current adapter position
         */
        override fun invoke(model: CrunchyRssMedia?) {
            with (binding) {
                entity = model
                mediaThumbnail.setOnClickListener {
                    onItemClick(it)
                }
                executePendingBindings()
            }
        }

        /**
         * If any image views are used within the view holder, clear any pending async requests
         * by using [com.bumptech.glide.RequestManager.clear]
         *
         * @see com.bumptech.glide.Glide
         */
        override fun onViewRecycled() {
            with(binding) {
                mediaThumbnail.onViewRecycled()
                unbind()
            }
        }

        /**
         * Handle any onclick events from our views, optionally you can call
         * [performClick] to dispatch [Pair]<[Int], T> on the [ItemClickListener]
         *
         * @param view the view that has been clicked
         */
        override fun onItemClick(view: View) {
            performClick(
                clickListener = clickListener,
                entity = binding.entity,
                view = view
            )
        }
    }
}