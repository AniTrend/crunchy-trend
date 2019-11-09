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
import android.provider.ContactsContract.Directory.PACKAGE_NAME
import co.anitrend.support.crunchyroll.core.naviagation.contract.INavigationRouter
import co.anitrend.support.crunchyroll.core.naviagation.contract.INavigationTarget
import co.anitrend.support.crunchyroll.core.naviagation.extensions.forIntent
import kotlinx.android.parcel.Parcelize

object NavigationTargets {

    object Main : INavigationRouter, INavigationTarget {
        private const val targetItem = "ui.activity.MainScreen"

        override val className = "$PACKAGE_NAME.$targetItem"
        override val navRouterIntent = forIntent()
    }

    object Splash : INavigationRouter, INavigationTarget {
        private const val targetItem = "feature.ui.activity.SplashScreen"

        override val className = "$PACKAGE_NAME.$targetItem"
        override val navRouterIntent = forIntent()
    }

    object Settings : INavigationRouter, INavigationTarget {
        private const val targetItem = "feature.settings.ui.activity.SettingsScreen"

        override val className = "$PACKAGE_NAME.$targetItem"
        override val navRouterIntent = forIntent()
    }

    object Authentication : INavigationRouter, INavigationTarget {
        private const val targetItem = "feature.authentication.ui.activity.AuthenticationScreen"

        override val className = "$PACKAGE_NAME.$targetItem"
        override val navRouterIntent = forIntent()
    }

    object News : INavigationRouter, INavigationTarget {
        private const val targetItem = "feature.listing.ui.fragment.FragmentFeedNewsList"

        override val className = "$PACKAGE_NAME.$targetItem"
        override val navRouterIntent = forIntent()
    }

    object Listing : INavigationRouter, INavigationTarget {
        private const val targetItem = "feature.listing.ui.fragment.FragmentMediaFeeList"

        override val className = "$PACKAGE_NAME.$targetItem"
        override val navRouterIntent = forIntent()
    }

    object MediaDetail : INavigationRouter, INavigationTarget {
        private const val targetItem = "feature.media.ui.activity.MediaDetailScreen"

        override val className = "$PACKAGE_NAME.$targetItem"
        override val navRouterIntent = forIntent()
    }

    object MediaPlayer : INavigationRouter, INavigationTarget {
        private const val targetItem = "feature.player.ui.activity.MediaPlayerScreen"

        override val className = "$PACKAGE_NAME.$targetItem"
        override val navRouterIntent = forIntent()


        const val PAYLOAD = "FragmentMediaStream:Payload"

        @Parcelize
        data class Payload(
            val mediaId: Int,
            val episodeThumbnail: String?
        ) : Parcelable
    }

    object Search : INavigationRouter, INavigationTarget {
        private const val targetItem = "feature.search.ui.activity.SearchScreen"

        override val className = "$PACKAGE_NAME.$targetItem"
        override val navRouterIntent = forIntent()
    }
}