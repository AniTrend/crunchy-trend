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

package co.anitrend.support.crunchyroll.navigation

import android.os.Parcelable
import co.anitrend.support.crunchyroll.domain.series.enums.CrunchySeriesBrowseFilter
import co.anitrend.support.crunchyroll.navigation.contract.INavigationProvider
import co.anitrend.support.crunchyroll.navigation.contract.NavigationRouter
import kotlinx.android.parcel.Parcelize
import org.koin.core.inject

object Main : NavigationRouter() {
    override val provider by inject<Provider>()

    interface Provider : INavigationProvider
}

object Splash : NavigationRouter() {
    override val provider by inject<Provider>()

    interface Provider : INavigationProvider
}

object Settings : NavigationRouter() {
    override val provider by inject<Provider>()

    interface Provider : INavigationProvider
}

object Authentication : NavigationRouter() {
    override val provider by inject<Provider>()

    interface Provider : INavigationProvider
}

object News : NavigationRouter() {
    override val provider by inject<Provider>()

    @Parcelize
    data class Payload(
        val guid: String,
        val title: String,
        val subTitle: String,
        val description: String?,
        val content: String,
        val publishDate: Long?
    ) : Parcelable

    interface Provider : INavigationProvider
}

object Listing : NavigationRouter() {
    override val provider by inject<Provider>()

    interface Provider : INavigationProvider
}

object Media : NavigationRouter() {

    override val provider by inject<Provider>()

    @Parcelize
    data class Payload(
        val collectionThumbnail: String?,
        val collectionName: String,
        val collectionId: Long
    ) : Parcelable

    interface Provider : INavigationProvider
}

object MediaPlayer : NavigationRouter() {

    override val provider by inject<Provider>()

    @Parcelize
    data class Payload(
        val mediaId: Long,
        val collectionName: String?,
        val collectionThumbnail: String?,
        val episodeTitle: String?,
        val episodeThumbnail: String?
    ) : Parcelable

    interface Provider : INavigationProvider
}

object Search : NavigationRouter() {
    override val provider by inject<Provider>()

    interface Provider : INavigationProvider
}

object Discover : NavigationRouter() {
    override val provider by inject<Provider>()

    @Parcelize
    data class Payload(
        val browseFilter: CrunchySeriesBrowseFilter,
        val filterOption: String = ""
    ) : Parcelable

    interface Provider : INavigationProvider
}

object Catalog : NavigationRouter() {
    override val provider by inject<Provider>()

    interface Provider : INavigationProvider
}

object Series : NavigationRouter() {
    override val provider by inject<Provider>()

    @Parcelize
    data class Payload(
        val seriesId: Long
    ) : Parcelable

    interface Provider : INavigationProvider
}

object Season : NavigationRouter() {
    override val provider by inject<Provider>()

    @Parcelize
    data class Payload(
        val seriesId: Long
    ) : Parcelable

    interface Provider : INavigationProvider
}

object ImageViewer : NavigationRouter() {
    override val provider by inject<Provider>()

    @Parcelize
    data class Payload(
        val imageSrc: String
    ) : Parcelable

    interface Provider : INavigationProvider
}