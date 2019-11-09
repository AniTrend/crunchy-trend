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

package co.anitrend.support.crunchyroll.core.naviagation

import android.os.Parcelable
import co.anitrend.support.crunchyroll.core.naviagation.contract.INavigationRouter
import co.anitrend.support.crunchyroll.core.naviagation.contract.INavigationTarget
import co.anitrend.support.crunchyroll.core.naviagation.extensions.forIntent
import kotlinx.android.parcel.Parcelize

object NavigationTargets {

    object Main : INavigationRouter, INavigationTarget {
        override val className = "ui.activity.MainScreen"
        override val navRouterIntent = forIntent()
    }

    object Splash : INavigationRouter, INavigationTarget {
        override val className = "feature.ui.activity.SplashScreen"
        override val navRouterIntent = forIntent()
    }

    object Settings : INavigationRouter, INavigationTarget {
        override val className = "feature.settings.ui.activity.SettingsScreen"
        override val navRouterIntent = forIntent()
    }

    object Authentication : INavigationRouter, INavigationTarget {
        override val className = "feature.authentication.ui.activity.AuthenticationScreen"
        override val navRouterIntent = forIntent()
    }

    object News : INavigationRouter, INavigationTarget {
        override val className = "feature.listing.ui.fragment.FragmentFeedNewsList"
        override val navRouterIntent = forIntent()
    }

    object Listing : INavigationRouter, INavigationTarget {
        override val className = "feature.listing.ui.fragment.FragmentMediaFeeList"
        override val navRouterIntent = forIntent()
    }

    object MediaDetail : INavigationRouter, INavigationTarget {
        override val className = "feature.media.ui.activity.MediaDetailScreen"
        override val navRouterIntent = forIntent()
    }

    object MediaPlayer : INavigationRouter, INavigationTarget {
        override val className = "feature.player.ui.activity.MediaPlayerScreen"
        override val navRouterIntent = forIntent()


        const val PAYLOAD = "FragmentMediaStream:Payload"

        @Parcelize
        data class Payload(
            val mediaId: Int,
            val episodeThumbnail: String?
        ) : Parcelable
    }

    object Search : INavigationRouter, INavigationTarget {
        override val className = "feature.search.ui.activity.SearchScreen"
        override val navRouterIntent = forIntent()
    }
}